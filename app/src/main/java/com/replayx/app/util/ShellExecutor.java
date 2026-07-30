package com.replayx.app.util;

import java.util.function.Consumer;

/**
 * Utilitário para executar comandos shell via Shizuku (root/shell permission).
 * Contém a lógica de transferência entre pacotes do Free Fire.
 */
public final class ShellExecutor {

    /**
     * Executa a transferência de arquivos entre dois pacotes do Free Fire.
     * Usa Shizuku para obter permissão shell e executar cp/permissons nos diretórios do app.
     */
    public static String executeTransfer(String sourcePkg, String targetPkg,
                                          String version, String sourceDir, String targetDir) {
        // A lógica real usa Shizuku para executar comandos shell
        // que copiam arquivos de sensibilidade entre os pacotes do Free Fire
        // Original: AbstractC1173k4.m3892u()
        // O comando real é ofuscado e depende do ambiente root/shell
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "sh", "-c",
                "ls /data/data/" + sourcePkg + "/files/" + sourceDir + "/" + version + " && " +
                "cp -r /data/data/" + sourcePkg + "/files/" + sourceDir + "/" + version +
                " /data/data/" + targetPkg + "/files/" + targetDir + "/" + version + " && " +
                "echo COPIADO_OK"
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                return "COPIADO_OK";
            } else {
                return "NAO_ENCONTRADO";
            }
        } catch (Exception e) {
            return "NAO_ENCONTRADO";
        }
    }

    /**
     * Executa comandos de otimização via shell (Shizuku).
     */
    public static void executeCommands(java.util.List<String> commands, Consumer<String> log) {
        for (String cmd : commands) {
            try {
                log.accept("exec: " + cmd);
                ProcessBuilder pb = new ProcessBuilder("sh", "-c", cmd);
                pb.redirectErrorStream(true);
                Process process = pb.start();
                java.io.BufferedReader reader = new java.io.BufferedReader(
                    new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    log.accept(line);
                }
                int exitCode = process.waitFor();
                log.accept("exit=" + exitCode);
            } catch (Exception e) {
                log.accept("[ERR] " + cmd + " -> " + e.getMessage());
            }
        }
    }
}
