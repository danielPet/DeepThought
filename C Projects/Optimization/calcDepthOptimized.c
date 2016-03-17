// CS 61C Fall 2015 Project 4

// include SSE intrinsics
#if defined(_MSC_VER)
#include <intrin.h>
#elif defined(__GNUC__) && (defined(__x86_64__) || defined(__i386__))
#include <x86intrin.h>
#endif

// include OpenMP
#if !defined(_MSC_VER)
#include <pthread.h>
#endif
#include <omp.h>

#include "calcDepthOptimized.h"
#include "calcDepthNaive.h"

/* DO NOT CHANGE ANYTHING ABOVE THIS LINE. */

#include <math.h>
#include <stdbool.h>
#include <stdio.h>
#include "utils.h"

#define ABS(x) (((x) < 0) ? (-(x)) : (x))

// Implements the displacement function
float displacementOptimized(int dx, int dy)
{
    //float squaredDisplacement = dx * dx + dy * dy;
    float displacement = sqrt(dx * dx + dy * dy);
    return displacement;
}

/*
omp_set_num_threads(x);
omp_get_num_threads();
omp_get_thread_num();*/


void calcDepthOptimized(float *depth, float *left, float *right, int imageWidth, int imageHeight, int featureWidth, int featureHeight, int maximumDisplacement)
{
    int remainderHeight = imageHeight - featureHeight;
    int remainderWidth = imageWidth - featureWidth;
    int range = 2*featureWidth+1;
    int start = featureWidth - (range%4) + 1;

    #pragma omp parallel for
    /* The two outer for loops iterate through each pixel */
    for (int y = 0; y < imageHeight; y++)
    {
        //#pragma omp parallel for
        for (int x = 0; x < imageWidth; x++)
        {   
            /* Set the depth to 0 if looking at edge of the image where a feature box cannot fit. */
            if ((x >= remainderWidth) || (x < featureWidth) || (y < featureHeight) || (y >= remainderHeight)) {
                depth[y * imageWidth + x] = 0;
                continue;
            }

            float minimumSquaredDifference = -1;
            int minimumDy = 0;
            int minimumDx = 0;

            int LYLX_partial_op = (y * imageWidth) + x;

            /* Iterate through all feature boxes that fit inside the maximum displacement box. 
               centered around the current pixel. */
            for (int dy = -maximumDisplacement; dy <= maximumDisplacement; dy++)
            {
                /* Skip feature boxes that dont fit in the displacement box. */
                if (y + dy - featureHeight < 0 || y + dy + featureHeight >= imageHeight) continue;

                int RYRX_dy_op = ((y + dy) * imageWidth) + x;

                for (int dx = -maximumDisplacement; dx <= maximumDisplacement; dx++)
                {
                    /* Skip feature boxes that dont fit in the displacement box. */
                    if (x + dx - featureWidth < 0 || x + dx + featureWidth >= imageWidth) continue;
                    
                    int RYRX_partial_op = RYRX_dy_op + dx;
                    float squaredDifference = 0;
                    __m128 squared_diff_sum = _mm_setzero_ps();

                    /* Sum the squared difference within a box of +/- featureHeight and +/- featureWidth. */
                    for (int boxY = -featureHeight; boxY < featureHeight; boxY+=2)
                    {
                        int LYLX = LYLX_partial_op + (boxY * imageWidth);
                        int RYRX = RYRX_partial_op + (boxY * imageWidth);
                        for (int boxX = -featureWidth; boxX <= featureWidth-3; boxX+=4)
                        {
                            __m128 nextLeft = _mm_loadu_ps(&left[LYLX + boxX]);
                            __m128 nextRight = _mm_loadu_ps(&right[RYRX + boxX]);
                            __m128 curr_diff = _mm_sub_ps(nextLeft, nextRight);
                            __m128 squared_diff = _mm_mul_ps(curr_diff, curr_diff);
                            squared_diff_sum = _mm_add_ps(squared_diff_sum, squared_diff);

                            nextLeft = _mm_loadu_ps(&left[LYLX + boxX + imageWidth]);
                            nextRight = _mm_loadu_ps(&right[RYRX + boxX + imageWidth]);
                            curr_diff = _mm_sub_ps(nextLeft, nextRight);
                            squared_diff = _mm_mul_ps(curr_diff, curr_diff);
                            squared_diff_sum = _mm_add_ps(squared_diff_sum, squared_diff);
                        }

                        for (int boxX = start; boxX <= featureWidth; boxX++) {
                            float difference1 = left[LYLX + boxX] - right[RYRX + boxX];
                            float difference2 = left[LYLX + boxX + imageWidth] - right[RYRX + boxX + imageWidth];
                            squaredDifference += (difference1 * difference1) + (difference2 * difference2);
                        }
                    }
                    //int boxY = featureHeight;
                    int LYLX = LYLX_partial_op + (featureHeight * imageWidth);
                    int RYRX = RYRX_partial_op + (featureHeight * imageWidth);
                    for (int boxX = -featureWidth; boxX <= featureWidth-3; boxX+=4)
                    {
                        __m128 nextLeft = _mm_loadu_ps(&left[LYLX + boxX]);
                        __m128 nextRight = _mm_loadu_ps(&right[RYRX + boxX]);
                        __m128 curr_diff = _mm_sub_ps(nextLeft, nextRight);
                        __m128 squared_diff = _mm_mul_ps(curr_diff, curr_diff);
                        squared_diff_sum = _mm_add_ps(squared_diff_sum, squared_diff);
                    }

                    for (int boxX = start; boxX <= featureWidth; boxX++) {
                        float difference = left[LYLX + boxX] - right[RYRX + boxX];
                        squaredDifference += difference * difference;
                    }
                    
                    squaredDifference += squared_diff_sum[0];
                    squaredDifference += squared_diff_sum[1];
                    squaredDifference += squared_diff_sum[2];
                    squaredDifference += squared_diff_sum[3];
                    
                    /* 
                    Check if you need to update minimum square difference. 
                    This is when either it has not been set yet, the current
                    squared displacement is equal to the min and but the new
                    displacement is less, or the current squared difference
                    is less than the min square difference.
                    */
                    if ((minimumSquaredDifference > squaredDifference) || (minimumSquaredDifference == -1))
                    {
                        minimumSquaredDifference = squaredDifference;
                        minimumDx = dx;
                        minimumDy = dy;
                        continue;
                    }
                    if (minimumSquaredDifference == squaredDifference) {
                        if (displacementOptimized(dx, dy) < displacementOptimized(minimumDx, minimumDy)) {
                            minimumSquaredDifference = squaredDifference;
                            minimumDx = dx;
                            minimumDy = dy;
                            continue;
                        }
                    }
                }
            }

            /* 
            Set the value in the depth map. 
            If max displacement is equal to 0, the depth value is just 0.
            */
            if (minimumSquaredDifference != -1) {
                if (maximumDisplacement == 0) {depth[y * imageWidth + x] = 0;}
                else {depth[y * imageWidth + x] = displacementOptimized(minimumDx, minimumDy);}
            }
            else {depth[y * imageWidth + x] = 0;}
        }
    }
}
