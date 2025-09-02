# Instruções de Uso e Execução do Aplicativo Médico de Família Digital

Este documento fornece um guia passo a passo para iniciantes sobre como usar e executar o aplicativo "Médico de Família Digital" em um dispositivo Android.

## 1. Pré-requisitos

Para executar o aplicativo, você precisará de:

*   Um computador com o Android Studio instalado. Você pode baixá-lo em [developer.android.com/studio](https://developer.android.com/studio).
*   Um dispositivo Android físico (smartphone ou tablet) ou um emulador Android configurado no Android Studio.
*   Conexão com a internet para o primeiro login e para funcionalidades que dependem do Firebase (autenticação, banco de dados, armazenamento).

## 2. Como Obter o Código-Fonte do Aplicativo

O código-fonte do aplicativo "Médico de Família Digital" será fornecido a você. Geralmente, ele é entregue como um arquivo compactado (.zip) ou através de um repositório Git. Supondo que você tenha o arquivo compactado:

1.  **Descompacte o arquivo:** Extraia o conteúdo do arquivo `.zip` para uma pasta de sua escolha no seu computador (ex: `C:\ProjetosAndroid\MedicoFamiliaDigital` ou `/home/usuario/ProjetosAndroid/MedicoFamiliaDigital`).

## 3. Abrindo o Projeto no Android Studio

1.  **Abra o Android Studio:** Inicie o Android Studio no seu computador.
2.  **Importar Projeto:** Na tela de boas-vindas do Android Studio, selecione "Open an existing Android Studio project" (Abrir um projeto Android Studio existente) ou, se já houver um projeto aberto, vá em `File > Open` (Arquivo > Abrir).
3.  **Navegue até a pasta do projeto:** Na janela de diálogo que se abre, navegue até a pasta onde você descompactou o código-fonte do aplicativo (ex: `MedicoFamiliaDigital`). Selecione a pasta raiz do projeto e clique em "OK" ou "Open" (Abrir).
4.  **Aguarde a Sincronização do Gradle:** O Android Studio irá começar a sincronizar o projeto com o Gradle. Isso pode levar alguns minutos, dependendo da sua conexão com a internet e da velocidade do seu computador. Você verá uma barra de progresso na parte inferior da janela do Android Studio. Certifique-se de que a sincronização seja concluída com sucesso.

## 4. Configurando o Firebase (Muito Importante!)

O aplicativo "Médico de Família Digital" utiliza o Firebase para autenticação, banco de dados e armazenamento. Para que ele funcione corretamente, você precisará configurar seu próprio projeto Firebase e conectá-lo ao aplicativo.

1.  **Crie um Projeto Firebase:**
    *   Vá para o [Console do Firebase](https://console.firebase.google.com/).
    *   Clique em "Add project" (Adicionar projeto) e siga as instruções para criar um novo projeto.
2.  **Adicione um Aplicativo Android ao seu Projeto Firebase:**
    *   No seu projeto Firebase, clique no ícone do Android (Android) para adicionar um novo aplicativo Android.
    *   Siga as instruções, fornecendo o `Package name` (Nome do pacote) do seu aplicativo. O nome do pacote para este projeto é `com.medicofamiliadigital`. Você pode encontrá-lo no arquivo `app/build.gradle` (campo `applicationId`).
    *   **Baixe o arquivo `google-services.json`:** Este arquivo é crucial. Coloque-o na pasta `app/` do seu projeto Android Studio (ex: `medico_familia_digital/app/google-services.json`).
3.  **Habilite os Serviços Firebase Necessários:**
    *   No Console do Firebase, vá em `Build > Authentication` (Compilar > Autenticação) e habilite o método de login "Google".
    *   Vá em `Build > Firestore Database` (Compilar > Banco de Dados Firestore) e crie um novo banco de dados. Escolha o modo de "Start in production mode" (Iniciar no modo de produção) e defina as regras de segurança para permitir leitura e escrita (para fins de teste inicial, você pode usar `allow read, write: if true;` mas **NÃO FAÇA ISSO EM PRODUÇÃO**).
    *   Vá em `Build > Storage` (Compilar > Armazenamento) e crie um novo bucket de armazenamento. Defina as regras de segurança para permitir leitura e escrita (para fins de teste inicial, você pode usar `allow read, write;` mas **NÃO FAÇA ISSO EM PRODUÇÃO**).

## 5. Executando o Aplicativo

### 5.1. Em um Emulador Android

1.  **Crie um Emulador (AVD):** No Android Studio, vá em `Tools > Device Manager` (Ferramentas > Gerenciador de Dispositivos). Clique em "Create device" (Criar dispositivo) e siga as instruções para criar um novo dispositivo virtual Android (AVD).
2.  **Selecione o Emulador:** Na barra de ferramentas superior do Android Studio, selecione o AVD que você acabou de criar no menu suspenso de dispositivos.
3.  **Execute o Aplicativo:** Clique no botão "Run 'app'" (Executar 'app') (o ícone de triângulo verde) na barra de ferramentas. O Android Studio irá compilar e instalar o aplicativo no emulador. Isso pode levar alguns minutos na primeira vez.

### 5.2. Em um Dispositivo Android Físico

1.  **Habilite a Depuração USB:** No seu dispositivo Android, vá em `Settings > About phone` (Configurações > Sobre o telefone) e toque em "Build number" (Número da versão) sete vezes para habilitar as "Developer options" (Opções do desenvolvedor). Em seguida, vá em `Settings > System > Developer options` (Configurações > Sistema > Opções do desenvolvedor) e habilite "USB debugging" (Depuração USB).
2.  **Conecte o Dispositivo:** Conecte seu dispositivo Android ao computador usando um cabo USB.
3.  **Autorize a Depuração:** Uma caixa de diálogo pode aparecer no seu dispositivo perguntando se você deseja "Allow USB debugging" (Permitir depuração USB). Toque em "OK" ou "Allow" (Permitir).
4.  **Selecione o Dispositivo:** Na barra de ferramentas superior do Android Studio, selecione seu dispositivo no menu suspenso de dispositivos.
5.  **Execute o Aplicativo:** Clique no botão "Run 'app'" (Executar 'app') (o ícone de triângulo verde) na barra de ferramentas. O Android Studio irá compilar e instalar o aplicativo no seu dispositivo.

## 6. Primeiro Uso do Aplicativo

Após o aplicativo ser instalado e iniciado:

1.  **Tela de Login:** Você será direcionado para a tela de login. Clique no botão "Entrar com Google" para autenticar-se usando sua conta Google.
2.  **Criação de Perfil:** Após o login, você será solicitado a criar um perfil. Preencha as informações necessárias (nome, idade, gênero, histórico médico, alergias).
3.  **Navegação:** Explore as diferentes seções do aplicativo:
    *   **Perfis:** Gerencie múltiplos perfis familiares.
    *   **Documentos:** Faça upload e visualize documentos médicos.
    *   **Lembretes:** Crie e gerencie lembretes para consultas, exames, etc.
    *   **Assistente de IA:** Converse com o assistente virtual para obter informações de saúde.
    *   **Painel de Saúde:** Acompanhe suas métricas de saúde.

## 7. Solução de Problemas Comuns

*   **"Gradle sync failed" (Sincronização do Gradle falhou):** Verifique sua conexão com a internet. Tente "File > Sync Project with Gradle Files" (Arquivo > Sincronizar Projeto com Arquivos Gradle).
*   **"google-services.json missing" (google-services.json ausente):** Certifique-se de que o arquivo `google-services.json` esteja na pasta `app/` do seu projeto e que você o baixou do seu projeto Firebase correto.
*   **Erros de compilação:** Verifique as mensagens de erro no painel "Build" (Compilação) do Android Studio. Eles geralmente indicam o problema e a linha de código onde ele ocorreu.
*   **Aplicativo não inicia no dispositivo/emulador:** Verifique se a depuração USB está ativada no dispositivo físico ou se o emulador está funcionando corretamente.

Se precisar de mais ajuda, por favor, me informe!

