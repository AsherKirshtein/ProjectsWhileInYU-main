#include <stdio.h>

int main()
{
    int letters[26];
    for(int i = 0; i < 26; i++)
    {
        letters[i] = 0;
    }
    char c; 
    while ((c = getchar()) != EOF && c != '\n')
    {
        if(c <= 90 || c >= 65)
        {
            int index = c-65;
            letters[index]++;
        }
        if(c <= 122 || c >= 97)
        {
            int index = c-97;
            letters[index]++;
        }
    }
    char a = 'a';
    for(int i = 0; i < 26; i++)
    {
        printf("\n %c = %d", a, letters[i]);
        a++;
    }
}