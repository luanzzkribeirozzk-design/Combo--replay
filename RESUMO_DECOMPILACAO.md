# Resumo da Decompilação — ReplayX (com.replayx.app)

## Informações Gerais do APK

| Campo | Valor |
|---|---|
| **applicationId** | `com.replayx.app` |
| **Nome exibido** | Netflix |
| **compileSdk** | 34 |
| **minSdk** | 29 |
| **targetSdk** | 34 |
| **Theme** | Theme.ReplayX (MaterialComponents.NoActionBar) |
| **Cor primária** | #ffd700 (dourado) |
| **Cor de fundo** | #080800 (preto com toque amarelado) |
| **extractNativeLibs** | false |
| **allowBackup** | false |

## Activities Identificadas

| Activity | Arquivo | Exportada | Função |
|---|---|---|---|
| LoginActivity | `com.replayx.app.ui.LoginActivity` | Sim (LAUNCHER) | Tela de login com validação de key |
| MainActivity | `com.replayx.app.ui.MainActivity` | Não | Tela principal (bypass, hide stream, timer) |
| SensiActivity | `com.replayx.app.ui.SensiActivity` | Não | Configurador de sensibilidade (WebView) |
| OtimizacaoActivity | `com.replayx.app.ui.OtimizacaoActivity` | Não | Otimizador Free Fire (54 comandos shell) |

## Services

| Serviço | Arquivo | Função |
|---|---|---|
| ReplayTransferService | `com.replayx.app.service.ReplayTransferService` | Transferência de dados entre pacotes FF MAX / FF Normal |

## Permissões Usadas

| Permissão | Uso |
|---|---|
| `android.permission.INTERNET` | Comunicação com servidor Firebase/Firestore |
| `android.permission.READ_EXTERNAL_STORAGE` | Leitura de arquivos (até SDK 32) |
| `android.permission.WRITE_EXTERNAL_STORAGE` | Escrita de arquivos (até SDK 29) |
| `moe.shizuku.manager.permission.API_V23` | Permissão Shizuku para execução de comandos shell |

## Provedores (Providers)

| Provider | Autoridade | Função |
|---|---|---|
| `rikka.shizuku.ShizukuProvider` | `com.replayx.app.shizuku` | Integração Shizuku para root/shell commands |

## Dependências / Bibliotecas Detectadas

| Biblioteca | Versão | Uso |
|---|---|---|
| androidx.appcompat:appcompat | 1.6.1 | AppCompatActivity |
| com.google.android.material:material | 1.11.0 | SwitchMaterial, botões |
| androidx.core:core-ktx | 1.12.0 | PointerIconCompat, ViewBindings |
| androidx.viewbinding:viewbinding | 8.2.2 | View Binding |
| dev.rikka.shizuku:api | 13.1.5 | Permissão shell/root |
| dev.rikka.shizuku:provider | 13.1.5 | ShizukuProvider |
| androidx.lifecycle:lifecycle-runtime-ktx | 2.7.0 | LifecycleOwnerKt |
| androidx.activity:activity-ktx | 1.8.2 | Activity compat |
| kotlin-stdlib | 1.9.22 | Kotlin runtime |

## Estrutura do Projeto Reconstruído

```
ReplayXProject/
├── build.gradle              (nível projeto)
├── settings.gradle
├── gradle.properties
├── local.properties
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
└── app/
    ├── build.gradle           (nível app)
    ├── proguard-rules.pro
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/replayx/app/
        │   ├── service/
        │   │   └── ReplayTransferService.java
        │   ├── ui/
        │   │   ├── LoginActivity.java
        │   │   ├── MainActivity.java
        │   │   ├── SensiActivity.java
        │   │   ├── OtimizacaoActivity.java
        │   │   ├── ParticleView.java
        │   │   ├── SensiJsInterface.java
        │   │   └── *ViewBinding.java (4 arquivos)
        │   └── util/
        │       ├── ShellExecutor.java
        │       ├── SecurityHelper.java
        │       ├── NetworkLogger.java
        │       └── TransferResult.java
        ├── res/
        │   ├── layout/        (4 layouts XML)
        │   ├── drawable/      (12 drawables XML + 1 PNG)
        │   ├── mipmap-{hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi}/
        │   ├── values/        (colors, strings, styles, public.xml)
        │   └── xml/           (network_security_config.xml)
        └── assets/
            └── sensi.html
```

## O que foi recuperado com sucesso (100%)

- **4 Activities** completas com lógica e UI
- **1 Service** (ReplayTransferService)
- **4 layouts XML** (activity_login, activity_main, activity_sensi, activity_otimizacao)
- **12 drawables XML** + 1 PNG (bg_volpp)
- **Mipmap ic_launcher** (5 densidades)
- **1 asset HTML** (sensi.html)
- **AndroidManifest.xml** completo
- **Theme e colors** do app
- **ViewBindings** para cada Activity

## O que foi reconstruído a partir da lógica decompilada

- **ShellExecutor.java** — encapsula execução de comandos shell (originalmente ofuscado em `p000.AbstractC1173k4`)
- **SecurityHelper.java** — verificações de assinatura e hash (originalmente ofuscado)
- **NetworkLogger.java** — envio de logs para Firestore (originalmente inline + ofuscado)
- **TransferResult.java** — classe utilitária de resultado
- **ParticleView.java** — view customizada de partículas decorativas

## Limitações e pontos para revisão manual

1. **Lógica de validação de key real** — O endpoint do servidor e o formato da requisição são ofuscados. A classe `NetworkLogger.java` e a lógica de `LoginActivity` foram reconstruídas com base na lógica decompilada, mas os valores exatos do Firestore (project ID, API key) precisam ser verificados no APK original.

2. **Verificação anti-debug/anti-tamper** — A classe `SecurityHelper.java` contém uma implementação simplificada. A verificação original usa comparação XOR de hash que é ofuscada. O comportamento foi preservado mas os valores de hash reais dependem do APK original.

3. **Comandos shell do Shizuku** — Os 54 comandos de otimização foram extraídos diretamente do código decompilado e estão corretos. Porém, a execução real depende do Shizuku estar ativo no dispositivo do usuário.

4. **sensi.html** — O arquivo HTML foi recuperado intacto. A interface JavaScript `SensiJsInterface` foi reconstruída com base na classe `C1819zc` decompilada.

5. **R.java e IDs de recursos** — Os IDs foram reconstruídos no `public.xml` com base nos valores do APK original. Os números podem variar levemente entre compilações.

6. **Classes p000 do APK original** — Todas as classes ofuscadas do pacote `p000` (AbstractC1173k4, C1150j, C1600o8, C1603ob, C1689s8, C1707t8, C1761w8, etc.) foram substituídas por classes com nomes legíveis no pacote `com.replayx.app.util`, mantendo o mesmo comportamento.

7. **Kotlin annotations @Metadata** — Foram removidas. O código foi convertido para Java puro, que é funcionalmente equivalente.

8. **ViewBinding** — Como o `build.gradle` usa `viewBinding true`, o Android Studio irá gerar automaticamente as classes de ViewBinding. Os arquivos `*ViewBinding.java` incluídos servem como referência e podem ser removidos após o primeiro build.

## Como abrir no Android Studio

1. Abra o Android Studio
2. Selecione **File → Open** e navegue até a pasta `ReplayXProject/`
3. Aguarde o Gradle sync (pode demorar alguns minutos na primeira vez)
4. Conecte um dispositivo Android ou use o emulador
5. Clique em **Run → Run 'app'**

## Notas importantes

- O app requer o **Shizuku** instalado no dispositivo para funcionar (bypass e otimizador)
- O tema do app está configurado como `Theme.ReplayX` com cores dourado/preto
- O nome do app aparece como "Netflix" no launcher (camuflagem)
- A aplicação não possui backup habilitado (`allowBackup="false"`)
