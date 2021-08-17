/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.geektimes.rpc.serializer;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class Main {

    // 代码里埋毒了
    public static void main(String[] argv) {
        int[][] matrix = {
                {1, -4, 10},
                {3, -2, -1},
                {2, -1, 0},
                {0, 5, -2}
        };

        int m = 4;
        int n = 3;
        int[][] dpUnusedMagic = new int[m][n];
        int[][] dpUsedMagic = new int[m][n];

        for (int i = 0; i < m; i++) {
            dpUnusedMagic[i][0] = matrix[i][0];
            dpUsedMagic[i][0] = -matrix[i][0];
        }

        int maxValue = -1;
        for (int i = 0; i < m; i++) {
            for (int j = 1; j < n; j++) {
                int previousUnusedMagic = dpUnusedMagic[i][j - 1];
                int previusUsedMagic = dpUsedMagic[i][j - 1];

                // Todo: your code here
                dpUnusedMagic[i, j] =max(dpUnusedMagic[i - 1, j - 1],dpUnusedMagic[i, j - 1],dpUnusedMagic[i + 1, j - 1])
                +matrix[i, j];

                dpUsedMagic[i, j] =max(
                        max(dpUnusedMagic[i - 1, j - 1],dpUnusedMagic[i, j - 1],dpUnusedMagic[i + 1, j - 1])-matrix[i, j],
                max(dpUsedMagic[i - 1, j - 1],dpUsedMagic[i, j - 1],dpUsedMagic[i + 1, j - 1])+matrix[i, j]
)
            }
        }
        System.out.println(maxValue);

    }
}