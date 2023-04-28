from socket import *
import sys

# boilerplate strings for a test message and an end-of-message marker
testMsg = "\r\n I love computer networks!"
endmsg = "\r\n.\r\n"

# the mail server is represented by a pair consisting of a network address and a port
# where your fake server will be listening.  Feel free to change the port if you want.
# I picked 2525 because the standard SMTP port is 25, but you need root permission to use
# a port number below 1024

mailserver = ('localhost', 2525)



#	Create socket called clientSocket and establish a TCP connection with mailserver 

#Fill in start

clientSocket = socket(AF_INET, SOCK_STREAM)
clientSocket.connect(mailserver)

#Fill in end

# Check for the initial connection message
recv = clientSocket.recv(1024).decode()
if recv[:3] != '220':
    print('220 reply not received from server.')
    sys.exit(-2) # -2 is arbitrary for an error abort

#	Send HELO command and print server response. 
heloCommand = 'EHLO Alice\r\n' 
clientSocket.send(heloCommand.encode())

bool = True
while bool:
    recv1 = clientSocket.recv(1024).decode() 
    lines = recv1.splitlines()
    for line in lines:
        if '-' not in line:
            bool = False
        

if recv1[:3] != '250':
    print('250 reply not received from server.')
    sys.exit(-2)
 
#	Send MAIL FROM command,  print and check server response.
#	Fill in start

mail = 'MAIL FROM: <test@localhost>\r\n'
clientSocket.send(mail.encode())
recv2 = clientSocket.recv(1024).decode()
print(recv2)
if recv2[:3] != '250':
    print('250 reply not received from server.')
    sys.exit(-2)

#	Fill in end

#	Send RCPT TO command,  print and check server response.
#	Fill in start

RCPT = 'RCPT TO: <test@localhost>\r\n'
clientSocket.send(RCPT.encode())
recv3 = clientSocket.recv(1024).decode()
print(recv3)
if recv3[:3] != '250':
    print('250 reply not received from server.')
    sys.exit(-2)

#	Fill in end

#	Send DATA command,  print and check server response.
#	Fill in start

data = 'DATA\r\n'
clientSocket.send(data.encode())
recv4 = clientSocket.recv(1024).decode()
print(recv4)

if recv4[:3] != '354':
    print('250 reply not received from server.')
    sys.exit(-2)

#	Fill in end

#	Send message data.
#	Fill in start
f = sys.argv[1]
print(sys.argv[1] + " is the file name")
file = open(f, 'r')
while True:
    words = file.read(4096)
    if words != '':
        #print(words)
        clientSocket.send(words.encode())
    else:
        break

#	Fill in end

#	Message ends with a single period on a line.
#	Fill in start
clientSocket.send(endmsg.encode())
recv6 = clientSocket.recv(1024).decode()
print(recv6)
if recv6[:3] != '250':
    print('250 reply not received from server.')
    sys.exit(-2)

#	Fill in end

#	Send QUIT command,  print and check server response.
#	Fill in start
quit = 'QUIT\r\n'
clientSocket.send(quit.encode())
recv7 = clientSocket.recv(1024).decode()
print(recv7)
if recv7[:3] != '221':
    print('221 reply not received from server.')
    sys.exit(-2)
#   Fill in end
