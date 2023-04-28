/* Generic */
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <pthread.h>
 
/* Network */
#include <netdb.h>
#include <sys/socket.h>
 
#define BUF_SIZE 100
 
char* url;
char* port;
char* path1;
char* path2;
// Get host information (used to establishConnection)
struct addrinfo *getHostInfo(char* host, char* port)
{
  int r;
  struct addrinfo hints, *getaddrinfo_res;
  // Setup hints
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  if ((r = getaddrinfo(host, port, &hints, &getaddrinfo_res)))
  {
    fprintf(stderr, "[getHostInfo:21:getaddrinfo] %s\n", gai_strerror(r));
    return NULL;
  }
  return getaddrinfo_res;
}
// Establish connection with host


int establishConnection(struct addrinfo *info)
{
  if (info == NULL) return -1;
  int clientfd;
  for (;info != NULL; info = info->ai_next)
  {
    if ((clientfd = socket(info->ai_family,info->ai_socktype, info->ai_protocol)) < 0)
    {
      perror("[establishConnection:35:socket]");
      continue;
    }
    if (connect(clientfd, info->ai_addr, info->ai_addrlen) < 0)
    {
      close(clientfd);
      perror("[establishConnection:42:connect]");
      continue;
    }
    freeaddrinfo(info);
    return clientfd;
  }
  freeaddrinfo(info);
  return -1;
}
// Send GET request
void GET(int clientfd, char *host, char *port, char *path)
{
  char req[1000] = {0};
  sprintf(req, "GET %s HTTP/1.1\r\nHost: %s:%s\r\n\r\n", path,host,port);
  send(clientfd, req, strlen(req), 0);
}
void *work(void *arga)
{
  int clientfd = establishConnection(getHostInfo(url, port));
  int connect_counts = 0;
  char buf[BUF_SIZE];
  int pathOn = 0;
  printf("connections: \n");
  while(1)
  {
    connect_counts++;
    if(pathOn == 0)
    {
      GET(clientfd, url, port, path1);
    }
    if(pathOn == 1)
    {
      GET(clientfd, url, port, path2);
    }
    while (recv(clientfd, buf, BUF_SIZE, 0) > 0)
    {
      fputs(buf, stdout);
      memset(buf, 0, BUF_SIZE);
    }
    if(pathOn == 0)
    {
      pathOn = 1;
    }
    else
    {
      pathOn = 0;
    }
  }
  close(clientfd);
  return NULL;
}
int main(int argc, char **argv)
{
  url = argv[1];
  port = argv[2];
  path1 = argv[5];
  if(argc == 7)
  {
    path2 = argv[6];
  }
  if (argc != 6 && argc != 7)
  {
    fprintf(stderr, "USAGE: ./httpclient <hostname> <port> <threads> <Schedule> <request path>\n");
    return 1;
  }
  // Establish connection with <hostname>:<port>
  for(int i = 0; i < atoi(argv[3]); i++)
  {
    pthread_t thread;
    pthread_create(&thread, NULL, work, NULL);
  }
  return 0;
}
 

