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
package org.web3j.sokt

enum class SolcArguments(val baseArg: String) {
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
    IGNORE_MISSING("--ignore-missing"),
    ;

    var params: (() -> String)? = null

    fun param(param: () -> String): SolcArguments {
        params = param
        return this
    }

    override fun toString(): String {
        return if (params != null) {
            "$baseArg ${params!!.invoke()}"
        } else {
            baseArg
        }
    }
}
