/* ackirst
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/*
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded.
 */
char transpose_submit_desc[] = "Transpose submission";

void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
    if(M == 32)
    {
        for (int index1 = 0; index1 < N; index1 += 8) 
        {
            for (int index2 = 0; index2 < M; index2 += 8) 
            {
                for (int index3 = index1; index3 < index1 + 8; index3++)
                {
                
                int block1 = A[index3][index2 + 7];
                B[index2 + 7][index3] = block1;

                int block2 = A[index3][index2 + 6];
                B[index2 + 6][index3] = block2;

                int block3 = A[index3][index2 + 5];
                B[index2 + 5][index3] = block3;

                int block4 = A[index3][index2 + 4];
                B[index2 + 4][index3] = block4;

                int block5 = A[index3][index2 + 3];
                B[index2 + 3][index3] = block5;

                int block6 = A[index3][index2 + 2];
                B[index2 + 2][index3] = block6;

                int block7 = A[index3][index2 + 1];
                B[index2 + 1][index3] = block7;

                int block8 = A[index3][index2];
                B[index2][index3] = block8;
                }
            }
        }
    }
    if(M == 64)
    {
        for (int index1 = 0; index1 < N; index1 += 8) 
        {
            for (int index2 = 0; index2 < M; index2 += 8)
            {
                for (int index3 = index1; index3 < index1 + 4; index3++)
                {
                    int block1 = A[index3][index2 + 7];
                    B[index2 + 0][index3 + 4] = block1;

                    int block2 = A[index3][index2 + 6];
                    B[index2 + 1][index3 + 4] = block2;

                    int block3 = A[index3][index2 + 5];
                    B[index2 + 2][index3 + 4] = block3;

                    int block4 = A[index3][index2 + 4];
                    B[index2 + 3][index3 + 4] = block4;

                    int block5 = A[index3][index2 + 3];
                    B[index2 + 3][index3] = block5;

                    int block6 = A[index3][index2 + 2];
                    B[index2 + 2][index3] = block6;

                    int block7 = A[index3][index2 + 1];
                    B[index2 + 1][index3] = block7;

                    int block8 = A[index3][index2];          
                    B[index2][index3] = block8;                    
                }

                for (int i = 0; i < 4; i++) 
                {
                    B[index2 + 4 + i][index1 + 3] = B[(index2 + 3) - i][index1 + 7];
                    B[index2 + 4 + i][index1 + 2] = B[(index2 + 3) - i][index1 + 6];
                    B[index2 + 4 + i][index1 + 1] = B[(index2 + 3) - i][index1 + 5];
                    B[index2 + 4 + i][index1 + 0] = B[(index2 + 3) - i][index1 + 4];
                    
                    int block1 = A[index1 + 7][index2 + 4 + i];
                    B[index2 + 4 + i][index1 + 7] = block1;

                    int block2 = A[index1 + 6][index2 + 4 + i];
                    B[index2 + 4 + i][index1 + 6] = block2;

                    int block3 = A[index1 + 5][index2 + 4 + i];
                    B[index2 + 4 + i][index1 + 5] = block3;

                    int block4 = A[index1 + 4][index2 + 4 + i];
                    B[index2 + 4 + i][index1 + 4] = block4;

                    int block5 = A[index1 + 7][index2 + 3 - i];
                    B[index2 + 3 - i][index1 + 7] = block5;

                    int block6 = A[index1 + 6][index2 + 3 - i];
                    B[index2 + 3 - i][index1 + 6] = block6;

                    int block7 = A[index1 + 5][index2 + 3 - i];
                    B[index2 + 3 - i][index1 + 5] = block7;

                    int block8 = A[index1 + 4][index2 + 3 - i];
                    B[index2 + 3 - i][index1 + 4] = block8;
                }
            }
        }
    }
    if(M == 61)
    {
        for (int index1 = 0; index1 < N; index1 += 8)
        {
            for (int index2 = 0; index2 < M; index2 += 8)
            {
                for (int index3 = index1; index3 < index1 + 8 && index3 < N; index3++)
                {
                    for (int index4 = index2; index4 < index2 + 8 && index4 < M; index4++) 
                    {
                        int block = A[index3][index4]; 
                        B[index4][index3] = block;
                    }
                }
            }
        }
    }
}
/*
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started.
 */

/*
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; j++) {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }

}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc);

    /* Register any additional transpose functions */
    registerTransFunction(trans, trans_desc);

}

/*
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++) {
        for (j = 0; j < M; ++j) {
            if (A[i][j] != B[j][i]) {
                return 0;
            }
        }
    }
    return 1;
}