#include "cachelab.h"
#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>

//Asher Kirshtein
//ackirsht

int v_flag = 0;
int h_flag = 0;

int hits = 0;
int misses = 0;
int evicts = 0;

int s;
int b;
int E;
int t;

char *trace;

int m = sizeof(long) * 8;

struct Line
{
        int valid;
        unsigned tag;
        struct Line *nextLine;
};

struct Set
{
        struct Line *firstLine;
        int setCapacity;
        struct Set *nextSet;
};

struct Cache
{
        struct Set *firstSet;
        int cacheSize;
};

void makeSet(struct Set *set, int setSize)//Like the constructor for your set.
{
        set->firstLine = NULL;
        set->nextSet = NULL;

        set->setCapacity = setSize;
}

int getLinesInSet(struct Set *set) //gives you the amount of lines in a given set
{
        struct Line *currentLine = set->firstLine;
        int lines = 0;
        while (currentLine != NULL)
        {
            lines++;
            if(currentLine-> nextLine == NULL)
            {
                break;
            }
            currentLine = currentLine->nextLine;
        }
        return lines;
}

void removeLastLineInSet(struct Set *set)//gets rid of the last line in your set
{
        struct Line *twoBefore = NULL;
        struct Line *previous = NULL;
        struct Line *currentLine = set->firstLine;

        while (currentLine != NULL)
        {
                twoBefore = previous;
                previous = currentLine;
                currentLine = currentLine->nextLine;
        }
        if (twoBefore == NULL)
        {
                set->firstLine = NULL;
        }
        else
        {
                twoBefore->nextLine = NULL;
        }
        if (previous == NULL)
        {
                return;
        }
        else
        {
                free(previous);
        }
}

void putLineInFrontOfSet(struct Set *set, struct Line *line)//adds a line to the front of the set when you have filled up space
{
        if (getLinesInSet(set) == set->setCapacity)
        {
                removeLastLineInSet(set);
                evicts++;
        }
        line->nextLine = set->firstLine;
        set->firstLine = line;
}

void makeLineFirst(struct Set *set, struct Line *line, struct Line *previous)//moves line to the front of the given set
{
        if (previous == NULL)
        {
                return;
        }
        previous->nextLine = line->nextLine;
        line->nextLine = set->firstLine;
        set->firstLine = line;
}

void makeCache(struct Cache *c, int RootSize, int setSize)//like a constructor for the cache struct
{
        struct Set *set = (struct Set*) malloc(sizeof(struct Set));

        c->firstSet = set;
        makeSet(set, setSize);
        c->cacheSize = (1 << RootSize);
        int size = c->cacheSize;

        for (int i = 1; i < size; i++)
        {
                struct Set *nextset = (struct Set*) malloc(sizeof(struct Set));
                makeSet(nextset, E);
                set->nextSet = nextset;
                set = nextset;
        }
}

void getFromCache(struct Cache *cache, unsigned a)//gets the address in the cache
{
        int sBits = (a << t) >> (t+b);
        int tBits = a >> (s+b);

        struct Set *targ = cache->firstSet;

        for (int i = 0; i < sBits; i++)
        {
                targ = targ->nextSet;
        }

        struct Line *previous = NULL;
        struct Line *line = targ->firstLine;

        while (line != NULL)
        {
                if (line->valid && (line->tag == tBits))
                {
                        
                        makeLineFirst(targ, line, previous);
                        hits++;
                        return;
                }
                previous = line;
                line = line->nextLine;
        }

        struct Line *newln = (struct Line *)malloc(sizeof(struct Line));
        
        newln->valid = 1;
        newln->tag = tBits;
        putLineInFrontOfSet(targ,newln);
        misses++;
}

int main(int argc, char** argv)
{
        int flag;
        while((flag = getopt(argc, argv, "hvs:b:t:E:")) != -1)//Check through the flags(getopt: a function for parsing arguements)
        {
                switch(flag)
                {
                        case 'h':
                                h_flag = 1;
                                break;
                        case 'v':
                                v_flag = 1;
                                break;
                        case 's':
                                //optarg is a pointer to a string(makes converting to int easier)
                                s = atoi(optarg);//atoi Parses the C-string str interpreting its content as an integral number, which is returned as a value of type int
                                break;
                        case 'E':
                                E = atoi(optarg);
                                break;
                        case 'b':
                                b = atoi(optarg);
                                break;
                        case 't':
                                trace = optarg;
                                break;
                }
        }
        t = m - (s + b);

        struct Cache *cache = (struct Cache*) malloc(sizeof(struct Cache));
        makeCache(cache, s, E);

        FILE *f;
        f = fopen(trace, "r");
        unsigned address;
        int size;
        char C;
        while(fscanf(f, "%c %x, %d", &C, &address, &size) > 0)
        {
                switch(C)
                {
                        case 'M':
                                getFromCache(cache, address);
                                hits++;
                                break;
                        case 'S':
                                getFromCache(cache, address);
                                break;
                        case 'L':
                                getFromCache(cache, address);
                                break;
                }
        }
        printSummary(hits, misses, evicts);
        return 0;
}
