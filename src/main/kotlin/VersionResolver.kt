import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.zafarkhaja.semver.Version
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list

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

    val testStrings =
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


    private fun getSolcReleases(): List<SolcRelease> {
        val (_, _, result) = Fuel.get("https://internal.services.web3labs.com/api/solidity/versions/")
            .header(Headers.ACCEPT, "application/json")
            .responseString()

        when (result) {
            is Result.Failure -> {
                val ex = result.getException()
                throw Exception("Failed to get solidity version from server", ex)
            }
            is Result.Success -> {
                val json = Json(JsonConfiguration.Stable)
                return json.parse(SolcRelease.serializer().list, result.get())
            }
        }
    }

    private fun versionsFromString(input: String): List<String> {
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