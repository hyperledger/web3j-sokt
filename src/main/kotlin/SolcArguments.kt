enum class SolcArguments (val baseArg: String) {
    AST_JSON("--ast-json"),
    AST_COMPACT_JSON("--ast-compact-json"),
    AST("--ast"),
    ASM_JSON("--asm-json"),
    OPCODES("--opcodes"),
    BIN("--bin"),
    BIN_RUNTIME("--bin-runtime"),
    ABI("--abi"),
    HASHES("--hashes"),
    USERDOC("--userdoc"),
    DEVDOC("--devdoc"),
    METADATA("--metadata"),

    OUTPUT_DIR("--output-dir"),
    OPTIMIZE("--optimize"),
    OPTIMIZE_RUNS("--optimize-runs"),
    OPTIMIZE_YUL("--optimize-yul"),
    NO_OPTIMIZE_YUL("--no-optimize-yul"),
    OVERWRITE("--overwrite"),
    COMBINED_JSON("--combined-json"),
    STANDARD_JSON("--standard-json"),
    ASSEMBLE("--assemble"),
    YUL("--yul"),
    MACHINE("--machine"),
    LINK("--link"),
    METADATA_HASH("--metadata-hash"),
    ALLOW_PATHS("--allow-paths"),
    IGNORE_MISSING("--ignore-missing");

    var params: (() -> String)? = null

    fun param(param: () -> String): SolcArguments {
        params = param;
        return this;
    }

    override fun toString(): String {
        return if (params != null) {
            "$baseArg ${params!!.invoke()}"
        } else {
            baseArg;
        }
    }


}