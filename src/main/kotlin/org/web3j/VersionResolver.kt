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

import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.result.Result
import com.github.zafarkhaja.semver.Version
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import java.nio.file.Paths

class VersionResolver {

    private val ver = Token("ver", "(\\d|\\.)+\\d*")
    private val hat = Token(null, "\\^")
    private val til = Token(null, "~")
    private val eq = Token(null, "=")
    private val lt = Token(null, ">")
    private val gt = Token(null, "<")
    private val ng = Token(null, "!")
    private val ignored = Token(null, "\\s+|;|v|(//.*)", true)

    private val tokenizer = DefaultTokenizer(listOf(ver, hat, til, eq, lt, gt, ng, ignored))

    private fun getSolcReleases(): List<SolcRelease> {
        val (_, _, result) = Fuel.get("https://internal.services.web3labs.com/api/solidity/versions/")
            .header(Headers.ACCEPT, "application/json")
            .responseString()
        val versionsFile = Paths.get(System.getProperty("user.home"), ".web3j", "solc", "releases.json").toFile()
        when (result) {
            is Result.Failure -> {
                if (versionsFile.exists()) {
                    return Json(JsonConfiguration.Stable).parse(SolcRelease.serializer().list, versionsFile.readText())
                }
                val ex = result.getException()
                throw Exception("Failed to get solidity version from server", ex)
            }
            is Result.Success -> {
                val resultText = result.get()
                versionsFile.parentFile.mkdirs()
                versionsFile.writeText(resultText)
                return Json(JsonConfiguration.Stable).parse(SolcRelease.serializer().list, resultText)
            }
        }
    }

    fun versionsFromString(input: String): List<String> {
        return tokenizer.tokenize(input).filter { !it.type.ignored }.fold(mutableListOf(mutableListOf<TokenMatch>()),
            { all, ele ->
                if (all.last().isNotEmpty() && all.last().last().type.name == "ver") {
                    all.add(mutableListOf(ele))
                } else {
                    all.last().add(ele)
                }
                all
            }).map { it.joinToString("") { nr -> nr.text } }
    }

    fun getCompatibleVersions(pragmaRequirement: String, releases: List<SolcRelease>): List<SolcRelease> {
        val requiredVersions = versionsFromString(pragmaRequirement)
        return releases.filter { requiredVersions.all { nr -> Version.valueOf(it.version).satisfies(nr) } }
    }

    fun getLatestCompatibleVersion(pragmaRequirement: String?): SolcRelease? {
        val solcReleases = getSolcReleases()
        return if (pragmaRequirement != null) {
            getCompatibleVersions(pragmaRequirement, solcReleases).lastOrNull()
        } else {
            solcReleases.last()
        }
    }
}
