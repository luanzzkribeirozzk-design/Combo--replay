# Build Automático via GitHub Actions

Este projeto já inclui um workflow do GitHub Actions configurado para compilar o APK automaticamente.

## Como configurar

### 1. Criar repositório no GitHub

```bash
cd ReplayXProject
git init
git add .
git commit -m "Initial commit: ReplayX project"
git branch -M main
git remote add origin https://github.com/SEU_USUARIO/ReplayX.git
git push -u origin main
```

### 2. O workflow executa automaticamente

Após o push para a branch `main` ou `master`, o GitHub Actions irá:
- Baixar JDK 17
- Fazer cache das dependências Gradle
- Compilar o projeto (debug + release)
- Fazer upload dos APKs como **artefatos** do build
- Se o push for na `main`, cria um **GitHub Release** com o APK anexado

### 3. Onde encontrar o APK

Após o build, acesse:

**Artefatos:** `Actions → Build APK → Downloads` (retém por 30 dias)

**Release:** `Releases` na aba do repositório (apenas push na main/master)

### 4. Trigger manual

Também é possível rodar o build manualmente:
- Vá em `Actions → Build APK → Run workflow → Run workflow`

### O que o workflow compila

| Tipo | Output | Assinatura |
|---|---|---|
| Debug | `app-debug.apk` | Debug key automática |
| Release | `app-release.apk` | Debug key (sem assinatura personalizada) |

### Como adicionar assinatura personalizada

Se quiser assinar com sua própria keystore:

1. Gere uma keystore: `keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias mykey`
2. Adicione as variáveis no repositório: `Settings → Secrets and Variables → Actions`:
   - `KEYSTORE_BASE64` (conteúdo da keystore em Base64)
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
3. Atualize o workflow para usar a keystore (exemplo abaixo)

### Estrutura do workflow

```
.github/workflows/build-apk.yml
```

O arquivo já está incluído no projeto. Não precisa fazer nenhuma alteração.
