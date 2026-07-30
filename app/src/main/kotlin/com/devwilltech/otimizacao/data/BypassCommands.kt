package com.devwilltech.otimizacao.data

object BypassCommands {

    const val FF_NORMAL = "com.dts.freefireth"
    const val FF_MAX    = "com.dts.freefiremax"

    // Versões atualizadas de cada jogo
    const val VER_FFN = "1.128.2"
    const val VER_FFM = "2.126.1"

    // Gera comando de transferência robusto (Android 10-17)
    // Tenta múltiplos caminhos em cascata
    private fun buildCommand(
        srcPkg: String, dstPkg: String,
        version: String, fromId: String, toId: String
    ): String {
        val fromIdEsc = fromId.replace(".", "\\.")
        return """
            # 1. Liberar permissoes via appops (Android 13+)
            cmd appops set $srcPkg READ_EXTERNAL_STORAGE allow 2>/dev/null
            cmd appops set $srcPkg MANAGE_EXTERNAL_STORAGE allow 2>/dev/null
            cmd appops set $srcPkg ACCESS_MEDIA_LOCATION allow 2>/dev/null
            cmd appops set $dstPkg WRITE_EXTERNAL_STORAGE allow 2>/dev/null
            cmd appops set $dstPkg MANAGE_EXTERNAL_STORAGE allow 2>/dev/null
            cmd appops set $dstPkg ACCESS_MEDIA_LOCATION allow 2>/dev/null

            # 2. Encontrar pasta de origem (multiplos caminhos)
            SRC=""
            for P in \
                "/storage/emulated/0/Android/data/$srcPkg/files/MReplays" \
                "/sdcard/Android/data/$srcPkg/files/MReplays" \
                "/data/media/0/Android/data/$srcPkg/files/MReplays" \
                "/data/data/$srcPkg/files/MReplays"; do
                [ -d "${"$"}P" ] && SRC="${"$"}P" && break
            done

            # 3. Encontrar pasta de destino
            DST=""
            for P in \
                "/storage/emulated/0/Android/data/$dstPkg/files/MReplays" \
                "/sdcard/Android/data/$dstPkg/files/MReplays" \
                "/data/media/0/Android/data/$dstPkg/files/MReplays" \
                "/data/data/$dstPkg/files/MReplays"; do
                DST="${"$"}P" && break
            done

            if [ -z "${"$"}SRC" ]; then echo "PASTA_NAO_ENCONTRADA"; exit 0; fi
            mkdir -p "${"$"}DST"

            # 4. Pegar replay mais recente
            BIN=$(ls -t "${"$"}SRC"/*.bin 2>/dev/null | head -n 1)
            JSON=$(ls -t "${"$"}SRC"/*.json 2>/dev/null | head -n 1)
            if [ -z "${"$"}BIN" ]; then echo "NAO_ENCONTRADO"; exit 0; fi

            BNAME=$(basename "${"$"}BIN")
            JNAME=$(basename "${"$"}JSON")

            # 5. Limpar destino e copiar bin
            rm -f "${"$"}DST"/*.bin "${"$"}DST"/*.json 2>/dev/null
            cp -f "${"$"}BIN" "${"$"}DST/${"$"}BNAME" && chmod 666 "${"$"}DST/${"$"}BNAME" || { echo "ERRO_COPIA_BIN"; exit 0; }
            chown $(stat -c '%u:%g' "${"$"}BIN") "${"$"}DST/${"$"}BNAME" 2>/dev/null

            # 6. Copiar e corrigir JSON
            if [ -n "${"$"}JSON" ]; then
                cp -f "${"$"}JSON" "${"$"}DST/${"$"}JNAME" && chmod 666 "${"$"}DST/${"$"}JNAME"
                chown $(stat -c '%u:%g' "${"$"}JSON") "${"$"}DST/${"$"}JNAME" 2>/dev/null
                sed -i 's/"Version":"[^"]*"/"Version":"$version"/g' "${"$"}DST/${"$"}JNAME" 2>/dev/null
                sed -i 's/"GameVersion":"[^"]*"/"GameVersion":"$version"/g' "${"$"}DST/${"$"}JNAME" 2>/dev/null
                sed -i 's/"AppId":"[^"]*"/"AppId":"$toId"/g' "${"$"}DST/${"$"}JNAME" 2>/dev/null
                sed -i 's/$fromIdEsc/$toId/g' "${"$"}DST/${"$"}JNAME" 2>/dev/null
            fi

            # 7. Forcar jogo destino reconhecer replay
            am force-stop $dstPkg 2>/dev/null
            cmd media scan-file "${"$"}DST/${"$"}BNAME" 2>/dev/null

            echo "SUCESSO"
        """.trimIndent()
    }

    fun getBypassMaxToNormalCommand(): String =
        buildCommand(FF_MAX, FF_NORMAL, VER_FFN, "freefiremax", "freefireth")

    fun getBypassNormalToMaxCommand(): String =
        buildCommand(FF_NORMAL, FF_MAX, VER_FFM, "freefireth", "freefiremax")
}
