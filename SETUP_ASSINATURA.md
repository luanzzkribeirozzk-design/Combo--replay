# Configurar assinatura de release (fazer 1 vez só)

O app agora builda como **release assinado** (não-debuggable, ofuscado ao
máximo, com verificação de assinatura em runtime). Pra isso funcionar no
GitHub Actions, você precisa cadastrar 4 Secrets no repositório.

## 1. Cadastrar os Secrets

No GitHub: **Settings → Secrets and variables → Actions → New repository secret**

Cadastre exatamente esses 4, com esses nomes (copiar certinho, maiúsculas incluídas):

| Nome do Secret          | Valor                                    |
|--------------------------|-------------------------------------------|
| `RX_KEYSTORE_B64`        | conteúdo do arquivo `replayx-release.jks.b64` (é uma linha só, cola inteira) |
| `RX_KEYSTORE_PASSWORD`   | `VFMpgI9M0ALuiIilyiGRdW6h`                |
| `RX_KEY_ALIAS`           | `replayx`                                 |
| `RX_KEY_PASSWORD`        | `VFMpgI9M0ALuiIilyiGRdW6h`                |

(senha da store e da key são a mesma de propósito — keystore no formato
PKCS12 exige isso)

## 2. Guardar a keystore original em local seguro

O arquivo `replayx-release.jks` (te mandei separado, **não** vai dentro do
zip do projeto) é a sua "chave mestra" de assinatura. Guarda ele em um
lugar seguro fora do GitHub (Google Drive particular, gerenciador de senha,
pendrive, etc.):

- **Se perder esse arquivo**, você nunca mais consegue publicar uma
  atualização assinada com a mesma identidade — quem já tem o app instalado
  precisaria desinstalar e instalar uma versão nova do zero (e a checagem
  de assinatura do `IntegrityCheck` ficaria com o hash desatualizado até
  você gerar uma keystore nova e recompilar essa constante).
- **Se alguém mais pegar esse arquivo + as senhas**, consegue assinar um
  APK malicioso que passa na checagem de integridade — trate como uma senha
  de banco.
- Nunca commite o `.jks` nem o `keystore.properties` no repositório (já
  adicionei os dois no `.gitignore` de proteção, mas cuidado ao copiar
  arquivos manualmente).

## 3. Rodar o build

Depois dos secrets cadastrados, é só dar push — o workflow já builda
`assembleRelease` automaticamente e assina com essa keystore. O APK final
sai em **Actions → run mais recente → Artifacts → ReplayX-APK**, já como
`app-release.apk`.

## O que isso resolve

- **Antes**: build `assembleDebug`, sem keystore fixa, `debuggable` efetivo
  = true (qualquer um conecta um debugger/Frida sem root e inspeciona tudo
  em runtime, inclusive strings decifradas em memória).
- **Agora**: build `assembleRelease`, `debuggable=false` forçado tanto no
  manifest quanto no build type, minify+shrink ativos, e o app **se recusa
  a abrir** se a assinatura do APK instalado não bater com o hash da sua
  keystore (`IntegrityCheck.hasExpectedSignature`) — ou seja, se alguém
  descompilar, alterar o código (tirar a checagem de key, mudar preço,
  etc.) e remontar o APK, ele precisa assinar com outra chave, e o app
  detecta isso e fecha sozinho antes de carregar a tela de login.
