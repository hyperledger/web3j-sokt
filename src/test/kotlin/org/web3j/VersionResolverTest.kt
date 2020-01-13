/*
 * Copyright 2020 Web3 Labs Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.web3j

import org.apache.commons.lang3.SystemUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VersionResolverTest {
    private val unsanitizedStrings =
        """
            >=0.5.10<0.5.14;
            >0.4.21 <=0.6.0;
            ^0.4.2;
            >=0.4.21   <0.6.0;
            >=0.4.21 <=0.6.0    ;
               >0.4.21 <0.6.0;
            >=0.4.21<0.6.0;
              ^ 0.4.21  ;
             ~ 0.4.21  ;
            0.4.2;
            >0.4.23 <0.5.0;
            0.4.0;
            v0.4.0; // like npm
            ^0.4.0;
            >= 0.4.0;
            <= 0.4.0;
            < 0.4.0;
            > 0.4.0;
            != 0.4.0;
            >=0.4.0 <0.4.8;
            0.4;
            v0.4;
            ^0.4;
            >= 0.4;
            <= 0.4;
            < 0.5;
            > 0.4;
            != 0.4;
            >=0.4 <=0.4;
            0;
            v0;
            ^0;
            >= 0;
            <= 0;
            < 1;
            > 0;
            != 0;
            >=0 <=1;
            ~0.4.24;
            ~0.4.24 >=0.5;
    """.trimIndent().split("\n")

    private val correctVersionConstraints =
        """
            >=0.5.10, <0.5.14
            >0.4.21, <=0.6.0
            ^0.4.2
            >=0.4.21, <0.6.0
            >=0.4.21, <=0.6.0
            >0.4.21, <0.6.0
            >=0.4.21, <0.6.0
            ^0.4.21
            ~0.4.21
            0.4.2
            >0.4.23, <0.5.0
            0.4.0
            0.4.0
            ^0.4.0
            >=0.4.0
            <=0.4.0
            <0.4.0
            >0.4.0
            !=0.4.0
            >=0.4.0, <0.4.8
            0.4
            0.4
            ^0.4
            >=0.4
            <=0.4
            <0.5
            >0.4
            !=0.4
            >=0.4, <=0.4
            0
            0
            ^0
            >=0
            <=0
            <1
            >0
            !=0
            >=0, <=1
            ~0.4.24
            ~0.4.24, >=0.5
        """.trimIndent().split("\n")

    private val correctVersions =
        """
            0.5.13
            0.5.14
            0.4.25
            0.5.14
            0.5.14
            0.5.14
            0.5.14
            0.4.25
            0.4.25
            null
            0.4.25
            null
            null
            0.4.25
            0.5.14
            null
            null
            0.5.14
            0.5.14
            null
            0.4.25
            0.4.25
            0.4.25
            0.5.14
            null
            0.4.25
            0.5.14
            0.5.14
            null
            0.5.14
            0.5.14
            0.5.14
            0.5.14
            null
            0.5.14
            0.5.14
            0.5.14
            0.5.14
            0.4.25
            null
        """.trimIndent().split("\n")

    private val resolver = VersionResolver()

    @Test
    fun correctVersionsFromStringsAreObtained() {
        unsanitizedStrings.forEachIndexed(fun(index: Int, s: String) {
            assertEquals(resolver.versionsFromString(s), correctVersionConstraints[index].split(", "))
        })
    }

    @Test
    fun correctSolidityVersionFromConstraintsIsResolved() {
        val releases = resolver.getSolcReleases()
        // Mac will not be able to do this as we don't have solc builds for every solc version tested here
        if (!SystemUtils.IS_OS_MAC) {
            unsanitizedStrings.forEachIndexed(fun(index: Int, s: String) {
                val correctVersion = correctVersions[index]
                val resolvedVersion = resolver.getCompatibleVersions(s, releases).lastOrNull()
                if (resolvedVersion == null) {
                    assertEquals("null", correctVersion)
                } else {
                    assertEquals(resolvedVersion.version, correctVersion)
                }
            })
        }
    }
}
