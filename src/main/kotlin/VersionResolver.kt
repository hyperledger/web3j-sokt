import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.DefaultTokenizer
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.lexer.TokenMatch
import com.github.zafarkhaja.semver.Version
import java.nio.file.Files
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



    private val solidityReleases = """
    0.4.12
    0.4.13
    0.4.14
    0.4.15
    0.4.16
    0.4.17
    0.4.18
    0.4.19
    0.4.20
    0.4.21
    0.4.22
    0.4.23
    0.4.24
    0.4.25
    0.5.0
    0.5.1
    0.5.2
    0.5.3
    0.5.4
    0.5.5
    0.5.6
    0.5.7
    0.5.8
    0.5.9
    0.5.10
    0.5.11
    0.5.12
    0.5.13
    0.5.14
""".trimIndent().split("\n").map { Version.valueOf(versionsFromString(it).first()) }

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

    fun getCompatibleVersions(pragmaRequirement: String): List<Version> {
        val requiredVersions = versionsFromString(pragmaRequirement)
        return solidityReleases.filter { requiredVersions.all { nr -> it.satisfies(nr) } }
    }

    fun getLatestCompatibleVersion(pragmaRequirement: String?): Version? {
        return if (pragmaRequirement != null) {
            getCompatibleVersions(pragmaRequirement).lastOrNull()
        } else {
            solidityReleases.last()
        }
    }
}