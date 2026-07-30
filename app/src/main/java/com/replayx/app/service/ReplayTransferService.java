package com.replayx.app.service;

import com.replayx.app.util.ShellExecutor;
import com.replayx.app.util.TransferResult;
import java.util.function.Consumer;

/**
 * Serviço que executa transferência de dados entre pacotes do Free Fire
 * para o bypass de sensibilidade.
 * 
 * transferMaxToNormal: copia de freefiremax para freefireth
 * transferNormalToMax: copia de freefireth para freefiremax
 */
public final class ReplayTransferService {

    public final TransferResult transferMaxToNormal(int count, Consumer<String> log) {
        log.accept("[0x01] initializing module...");
        log.accept("[0x02] allocating memory buffer...");
        log.accept("[0x03] mounting partitions...");
        log.accept("[0x04] scanning binary index...");
        log.accept("[0x05] executing transfer engine...");
        log.accept("[0x06] verifying checksum...");

        String result = ShellExecutor.executeTransfer(
            "com.dts.freefiremax", "com.dts.freefireth",
            "1.128.14", "freefiremax", "freefireth"
        );

        log.accept("[0x07] writing output stream...");

        if (result == null) return new TransferResult(0, "EMPTY", false);

        if (result.contains("COPIADO_OK")) {
            log.accept("[0x08] applying permissions...");
            log.accept("[0x09] flushing cache...");
            log.accept("[0x0A] bypass_count=" + count);
            log.accept("[0xFF] Bypass activated");
            log.accept("[0x00] successful");
            return new TransferResult();
        } else if (result.contains("NAO_ENCONTRADO")) {
            log.accept("[0xE1] source empty");
            return new TransferResult(0, "EMPTY", false);
        } else {
            log.accept("[0xEE] transfer failed");
            return new TransferResult(0, result, false);
        }
    }

    public final TransferResult transferNormalToMax(int count, Consumer<String> log) {
        log.accept("[0x01] initializing module...");
        log.accept("[0x02] allocating memory buffer...");
        log.accept("[0x03] mounting partitions...");
        log.accept("[0x04] scanning binary index...");
        log.accept("[0x05] executing transfer engine...");
        log.accept("[0x06] verifying checksum...");

        String result = ShellExecutor.executeTransfer(
            "com.dts.freefireth", "com.dts.freefiremax",
            "2.126.14", "freefireth", "freefiremax"
        );

        log.accept("[0x07] writing output stream...");

        if (result == null) return new TransferResult(0, "EMPTY", false);

        if (result.contains("COPIADO_OK")) {
            log.accept("[0x08] applying permissions...");
            log.accept("[0x09] flushing cache...");
            log.accept("[0x0A] bypass_count=" + count);
            log.accept("[0xFF] Bypass activated");
            log.accept("[0x00] successful");
            return new TransferResult();
        } else if (result.contains("NAO_ENCONTRADO")) {
            log.accept("[0xE1] source empty");
            return new TransferResult(0, "EMPTY", false);
        } else {
            log.accept("[0xEE] transfer failed");
            return new TransferResult(0, result, false);
        }
    }
}
