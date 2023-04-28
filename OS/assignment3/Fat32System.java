import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Fat32System 
{
	
	private static final int RootFat = 2;
	private static final int BPB_bytsPerSec = 512;

	private static short BPB_RsvdSecCnt;
	private static short BPB_NumFATs;
	private static int BPB_FATSz32;
	private static short BPB_SecPerClus;

	private static FatFile32 rootFile;
	private static int fat32Start;
	private static int clusterStart;
	private static int sizeOfSectors;

	private static boolean running;

	private static int currentFat;

	private static String output = "";

	static File pwd = new File(File.separator);

	static RandomAccessFile randomAccessFile;

	public static void main(String[] args) throws IOException 
	{
		if(args.length == 0)
		{
			System.out.println("\nYou didn't provide a file");
			System.out.println("Running with Default File: fat32.img\n" );
			args = new String[]{"fat32.img"};
		}
		Scanner scanner = new Scanner(System.in);
		Fat32System fat32 = new Fat32System(args[0]);
		running = true;
		while (running)
		{
			System.out.print(Fat32System.pwd.toString() + "] ");
			String command = scanner.nextLine();
			String[] cmd = command.split(" ");
			parseCommand(cmd, fat32);
		}
			scanner.close();
	}
	
	private static void stop()
	{
		System.out.println("Stopping");
		running = false;
	}


	private static void info()
	{
		System.out.println("BPB_BytesPerSec  0x" +  Integer.toHexString(BPB_bytsPerSec) + ", " + BPB_bytsPerSec); 
 		System.out.println("BPB_SecPerClus   0x" + Integer.toHexString(BPB_SecPerClus) + ", " + BPB_SecPerClus);  
 		System.out.println("BPB_RsvdSecCnt   0x" + Integer.toHexString(BPB_RsvdSecCnt) + ", " + BPB_RsvdSecCnt);  
 		System.out.println("BPB_NumFATS      0x" + Integer.toHexString(BPB_NumFATs) + ", " + BPB_NumFATs); 
 		System.out.println("BPB_FATSz32      0x" + Integer.toHexString(BPB_FATSz32) + ", " + BPB_FATSz32); 
	}

	public static void ls(String director_path) throws IOException 
	{
		FatFile32 dir;
		if(director_path.isEmpty()) 
		{
			dir = getFatFile(pwd);
		} 
		else 
		{
			dir = getFatFile(new File(director_path));
		} 
		output = "";
		for (FatFile32 file : directoryFiles(dir.cluster)) 
		{
			output += file.filename + " ";
		}
		System.out.println(output.toString());
		output = "";
	}

	
	private static void stat(String cmd, Fat32System fat32) throws IOException 
	{
		System.out.print("Size is "); 
		size(cmd);
	}

	public static void size(String file) throws IOException
	{
		FatFile32 fatFile = getFatFile(new File(file));
		if (fatFile == null) 
		{
			output = "";
			return;
		}
		System.out.println(fatFile.size);
	}

	public static void cd(String directory) throws IOException
	{
		FatFile32 fatFile = getFatFile(new File(directory));
		if (fatFile == null || !fatFile.directory) 
		{
			output = "";
			System.out.println( directory + " is not a directory");
		}
		
		if(fatFile.cluster != 0)
		{
			currentFat = fatFile.cluster; 
		} 
		else
		{
			currentFat = 2;
		}
		pwd = new File(output.toString());
		output = "";
	}

	public static void read(String filename, long ofst, long numberOfBytes) throws IOException
	{
		FatFile32 FatFile32 = getFatFile(new File(filename));
		if (FatFile32 == null || FatFile32.directory) 
		{
			System.out.println(filename + " is not a file");
		}

		if(numberOfBytes < 1)
		{
			System.out.println("Error: NUMBER_BYTES must be greater than 0");
		}

		if(ofst < 0)
		{
			System.out.println("Error: OFFSET must be a positive value");
		}
		
		if(ofst == numberOfBytes)
		{
			System.out.println("Error: file is not open");
		}
		if (FatFile32.size < (ofst + numberOfBytes)) 
		{
			System.out.println("Error: attempt to read data outside of file bounds ");
		}

		int clusterLocation = FatFile32.cluster;

		while (ofst >= sizeOfSectors) 
		{
			int seekLocal = fat32Start + 4 * (clusterLocation);
			randomAccessFile.seek(seekLocal);
			clusterLocation = Integer.reverseBytes(randomAccessFile.readInt());;
			ofst -= sizeOfSectors;
		}

		byte[] buffer = new byte[sizeOfSectors];
		
		while ((clusterLocation & 0x0FFFFFF8) != 0x0FFFFFF8) 
		{
			clusterReader(clusterLocation, buffer);
			for (int i = (int) ofst; i < buffer.length && numberOfBytes > 0; i++, numberOfBytes--) 
			{
				int current = Byte.toUnsignedInt(buffer[i]);
				if (current < 128)
				{
					output +=(char) current;
				} 
				else 
				{
					output += "0x" + Integer.toHexString(current);
				}
				ofst = 0;
			}
			randomAccessFile.seek(fat32Start + 4 * (clusterLocation));
			clusterLocation = Integer.reverseBytes(randomAccessFile.readInt());
		}
		String s = output.toString(); 
		output = "";
		System.out.println(s.replace("\\" + filename.toUpperCase(), ""));
	}

	static class FatFile32
	{
		final String filename;
		final int size;

		final boolean ro;
		final boolean notVisible;
		final boolean sys;
		final boolean vol;
		final boolean directory;
		final boolean archive;

		int cluster;
		byte attr; 

		private FatFile32(byte[] ogBytes, int ofst) 
		{
			String name = new String(ogBytes, ofst, 8);
			String fileType = new String(ogBytes, ofst + 8, 3);
			name = name.strip();
			fileType = fileType.strip();

			if (!fileType.isBlank())
			{
				fileType = '.' + fileType;
			}

			filename = name + fileType;

			attr = ogBytes[6 + ofst + 5];

			ro = ((attr & 1) == 1);
			attr >>= 1;

			notVisible = ((attr & 1) == 1);
			attr >>= 1;

			sys = ((attr & 1) == 1);
			attr >>= 1;

			vol = ((attr & 1) == 1);
			attr >>= 1;

			directory = ((attr & 1) == 1);
			attr >>= 1;

			archive = ((attr & 1) == 1);
			attr >>= 1;

			cluster = ((0xFF & ogBytes[ofst + 21]) << 24) | ((0xFF & ogBytes[ofst + 20]) << 16)
					| ((0xFF & ogBytes[ofst + 27]) << 8) | (0xFF & ogBytes[ofst + 26]);

			size = ((0xFF & ogBytes[ofst + 31]) << 24) | ((0xFF & ogBytes[ofst + 30]) << 16)
					| ((0xFF & ogBytes[ofst + 29]) << 8) | (0xFF & ogBytes[ofst + 28]);
		}
	}

	public Fat32System(String fileName) throws IOException 
	{
		byte[] bytes = Files.readAllBytes(Paths.get(fileName));

		ByteBuffer buffer = ByteBuffer.allocate(512);
		buffer.put(bytes, 0, 512);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		randomAccessFile = new RandomAccessFile(new File(fileName), "r");
		
		BPB_SecPerClus = buffer.get(0x0D);
		BPB_RsvdSecCnt = buffer.get(0x0E);
		BPB_NumFATs = buffer.getShort(0x10);
		BPB_FATSz32 = buffer.getInt(0x24);

		sizeOfSectors = BPB_SecPerClus * BPB_bytsPerSec;

		clusterStart = (BPB_RsvdSecCnt + (BPB_NumFATs * BPB_FATSz32)) * BPB_bytsPerSec;
		fat32Start = BPB_RsvdSecCnt * BPB_bytsPerSec;

		randomAccessFile.seek(clusterStart);

		currentFat = RootFat;
		byte[] b = new byte[52];

		clusterReader(RootFat, b);

		for (int i = 0; i < 11; i++) 
		{
			b[i] = ' ';
		}
		b[26] = 2;
		rootFile = new FatFile32(b, 0);
	}


	public static FatFile32 getFatFile(File file) throws IOException 
	{
		if (file.toString().equals(File.separator))
		{
			output +=File.separatorChar;
			return rootFile;
		}
		int currentPos = rightFile(file) ? RootFat : currentFat;
		File movingDir = rightFile(file) ? new File(File.separator) : pwd;
		FatFile32 returnDirectory = null;

		goTo: for (Path path : file.toPath()) 
		{
			String pathName = path.toString();

			if (pathName.equals(".")) 
			{
				continue;
			}

			if (returnDirectory != null && !returnDirectory.directory) 
			{
				output += returnDirectory.filename + " is not a directory";
				return null;
			}

			for (FatFile32 directoryFile : directoryFiles(currentPos)) 
			{
				if (directoryFile.filename.equalsIgnoreCase(pathName)) 
				{
					currentPos = directoryFile.cluster;
					returnDirectory = directoryFile;
					if(!pathName.equals(".."))
					{
						movingDir = new File(movingDir, directoryFile.filename);
					}
					else  
					{
						if (directoryFile.cluster < 1) 
						{
							directoryFile.cluster = 2;
						}
						movingDir = movingDir.getParentFile();
					} 
					continue goTo;
				}
			}

			output += "Could not find path: " + pathName + " in path: " + file;
			return null;
		}

		output += movingDir.toString();
		return returnDirectory;

	}

	private static byte[] clusterReader(int clusterLocation, byte[] buffer) throws IOException 
	{
		int oneSec = BPB_bytsPerSec * BPB_SecPerClus;
		if (buffer == null) 
		{
			buffer = new byte[oneSec];
		}
		randomAccessFile.seek(clusterStart +  (long) oneSec * (clusterLocation - 2));
		randomAccessFile.read(buffer, 0, buffer.length);
		return buffer;
	}

	private static List<FatFile32> directoryFiles(int dir) throws IOException
	{
		byte[] buffer = new byte[sizeOfSectors];
		List<FatFile32> fat32files = new ArrayList<FatFile32>();
		while ((dir & 0x0FFFFFF8) != 0x0FFFFFF8)
		{
			clusterReader(dir, buffer);
			for (int i = 0; i < sizeOfSectors; i += 32) 
			{
				if (dir == 2 && i == 0) 
				{
					for (int j = 0; j < 11; j++) 
					{
						buffer[j] = ' ';
					}
					buffer[0] = '.';
					buffer[1] = '.';
					FatFile32 df = new FatFile32(buffer, i);
					fat32files.add(df);
					buffer[1] = ' ';
					buffer[26] = 2;
					df = new FatFile32(buffer, i);
					fat32files.add(df);

					continue;
				}
				if (buffer[i] == 'A' && ((buffer[i + 11]) & 0x0F) == 0x0F) 
				{
					continue;
				}
				if (buffer[i] == (byte) 0xE5) 
				{
					continue;
				}
				if (buffer[i] == 0) 
				{
					return fat32files;
				}

				FatFile32 df = new FatFile32(buffer, i);
				fat32files.add(df);
			}
			randomAccessFile.seek(fat32Start + 4 * (dir));
			dir = Integer.reverseBytes(randomAccessFile.readInt());
		}
		return fat32files;
	}

	private static boolean rightFile(File file)
	{
		 if(file.toString().startsWith(File.separator) == true)
		 {
			 return true;
		 }
		 return false;
	}

	private static void parseCommand(String [] cmd, Fat32System fat32) throws NumberFormatException, IOException
	{
		switch (cmd[0].toLowerCase())
		{
			case "stop":
				stop();
				break;
			case "info":
				info();
				break;
			case "ls":
				if(cmd.length == 1)
				{
					ls("");
				}
				else if (cmd[1].equals("."))
				{
					ls("");
				} 
				else if (cmd.length > 1)
				{
					ls(cmd[1]);
				} 
				else 
				{
					ls("");
				}
				break;
			case "stat":
				stat(cmd[1], fat32);
				break;
			case "size":
				if(cmd.length < 2)
				{
					System.out.println("No file name");
					break;
				}
				size(cmd[1]);
				break;
			case "cd":
				cd(cmd[1]);
				break;
			case "read":
				if(cmd.length < 4)
				{
					System.out.println("Not enough input");
					break;
				}
				read(cmd[1], Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
				break;
			default:
				System.out.println("Invalid command");
				break;
			}
	}

}