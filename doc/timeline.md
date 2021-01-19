# Timeline

<h1>Configuração inicial</h1>

<h3>Projeto e Deps <a href="https://github.com/paygoc6/AuauPI/pull/6">fe674c08418daefe80dd0f367957ac43f2da7f06</a></h3>
<p>Foi criado um projeto em leningen e inseridas as dependências iniciais</p>

<h3>Libs <a href="https://github.com/paygoc6/AuauPI/pull/7">43bc4e7af0a433db63a99a1c54058996fa554903</a></h3>
<p>Foram inseridas as bibliotecas do pedestal, clj-http, data-json e matcher-combinators</p>

<h3>Estrutura do Atom <a href="https://github.com/paygoc6/AuauPI/pull/8">b8c44aca0adfb7cb3396c1eb7f527e36df2de0f1</a></h3>
<p>Criado o primeiro modelo de atom do projeto, que servirá como um banco de dados inicial, e inseridos alguns cachorros para testes futuros</p>

<h3>Pedestal <a href="https://github.com/paygoc6/AuauPI/pull/9">efba76b4e60c7a3ed3b4091795080cb1680f3c09</a></h3>
<p>Realizada a configuração do servidor do pedestal, com as funções de start e stop</p>

<h1>Rotas e funcionalidades do projeto</h1>

<h3>rota GET de todos os cachorros <a href="https://github.com/paygoc6/AuauPI/pull/10">d4ccb3e9fcfe74a5afe9ce8344eb5a18eeb751d4</a></h3>
<p>Neste commit, foi criada a rota GET que retorna informações resumidas de todos os cachorros que estão disponĩveis para a adoção, como uma foto, nome, raça, etc</p>

<h3>Hot-reload <a href="https://github.com/paygoc6/AuauPI/pull/11">bcb5cde63a2ab923f608cf785eea04d673448978</a></h3>
<p>Aqui foi inserida a função de hot reload, que tem como finalidade a agilização do processo de atualização do projeto, evitando que seja necessário reiniciar o REPL sempre que for realizada uma atualização</p>
<p>Também começamos a separar as funções do projeto, de servidor e de banco de dados</p>

<h3>Teste da rota GET <a href="https://github.com/paygoc6/AuauPI/pull/12">277a66a1bdc4d8ccb51f075e69179b3f4c7ec517</a></h3>
<p>Primeiros testes sendo realizados, para verificar se a função GET retorna exatamente o que era esperado</p>

<h3>Integração com a DOG.ceo <a href="https://github.com/paygoc6/AuauPI/pull/14">84051c776d97d6709a280264d1ee90cb587a52d7</a></h3>
<p>Adicionada a função que faz a integração com a API dog.ceo, buscando uma imagem aleatória da raça do cachorro cadastrado</p>

<h3>Spec <a href="https://github.com/paygoc6/AuauPI/pull/18">b3d3c6416c6793b4ee33b5e248b164574da1d2bc<</a></h3>
<p>Criando a validação dos dados cadastrados, com essa função, os dados dos cachorros inseridos devem obrigatoriamente ser do formato aceito(idade ser um número, nome ser um texto, etc)</p>
<p>Alguns dos parametros para o cadastro dos cachorros são opcionais, como o nome, caso o cachorro cadastrado não possua um</p>

<h3>Reformulação de filtragem <a href="https://github.com/paygoc6/AuauPI/pull/19">b3d3c6416c6793b4ee33b5e248b164574da1d2bc</a></h3>
<p>Com esta alteração, agora é possível buscar cachorros com parâmetros específicos, como buscar apenas por uma idade, ou apenas cachorros de um gênero específico</p>

<h3>Build do projeto <a href="https://github.com/paygoc6/AuauPI/pull/20">7a4f1d59111cb9919c7f673eb56fc8955f261b0c</a></h3>
<p>Preparando o projeto para poder ser buildado</p>

<h3>Correção do filtro <a href="https://github.com/paygoc6/AuauPI/pull/21">0dfcbb82ee481863b5034ff343a384618aa67081</a></h3>
<p>A partir disto, o filtro para busca de cachorros pode conter multiplos argumentos, em vez de apenas um</p>

<h3>Testes de lógica <a href="https://github.com/paygoc6/AuauPI/pull/22">216880d9d724197f8c79e8894e5606ce276f5bea</a></h3>
<p>Adicionados multiplos teste para testar várias possibilidades envolvendo os filtros de busca</p>

<h3>Separação de NameSpaces <a href="https://github.com/paygoc6/AuauPI/pull/23">bb971529de866230b7033ad8e131d0c6d4d0581b</a></h3>
<p>As funções do projeto foram divididas entre lógica, responsável pelas funções de regra do negócio, db, funções de configuração do banco de dados, e specs, funções relacionadas a validação de busca</p>

<h3>Teste de rota POST <a href="https://github.com/paygoc6/AuauPI/pull/24">823d0a141a2ce1d76a11502cd7d292851aa8c8a2</a></h3>
<p>Criados os testes para verificar o formato dos dados inseridos, o status de retorno e um GET para veficicar a inserção de um novo cachorro no BD</p>

<h3>Reajuste na rota GET <a href="https://github.com/paygoc6/AuauPI/pull/25">f83ef37cebf7d3ed5370eb101b4468d07b6589e6</a></h3>
<p>Quando um dos pârametros de busca era um boolean ou um número, gerava conflito com o próximo parâmetro</p>

<h3>Rota POST para cadastro de cachorro <a href="https://github.com/paygoc6/AuauPI/pull/26">1625d988944651a29f29e0b130c3147054a67217</a></h3>
<p>A partir disso, é possível cadastrar um cachorro enviando um pacote json, sendo os campos raça, gênero e tamanho obrigatórios</p>

<h3>rota GET para consulta de um cachorro especĩfico <a href="https://github.com/paygoc6/AuauPI/pull/27">9178dba9f85414f0b4cb13cdca597c15983e1c76</a></h3>
<p>Com essa rota, é possível visualizar todos os dados de um cachorro especĩfico (mais dados do que a rota GET padrão), e caso o ID não seja localizado, o retorno é o status 404</p>

<h3>Rota POST para adoção de um cachorro <a href="https://github.com/paygoc6/AuauPI/pull/28">1b33d3891d1d912d1efaabdf5e05b7ec0ea36159</a></h3>
<p>Adicionada a rota que faz a adoção de um cachorro, alterando o campo adopted? false para true, e devolvendo uma mensagem de adoção realizada com sucesso</p>

<h3>Correção de bug de cadastro <a href="https://github.com/paygoc6/AuauPI/pull/29">54804eb1a6a6071931a0094acb9a76e53f4d3ea3</a></h3>
<p>Corrigido um bug em que ao cadastrar um cachorro sem os parâmetros opcionais, eles se tornavam nulos e quebravam a aplicação em algumas situações</p>

<h3>Armazenamento de raças <a href="https://github.com/paygoc6/AuauPI/pull/31">ed0bdb27d7e0ac40a66713962675c5d46a5373d8</a></h3>
<p>As raças da api dog.ceo são armazenadas em um atom, assim, futuramente haverá uma validação de raças permitidas para cadastro, evitando futuras quebras na aplicação</p>

<h3>Separação de testes de funções <a href="https://github.com/paygoc6/AuauPI/pull/32">5a690b471a815de6efd56c3324b3d0a2dc20c6f0</a></h3>
<p>Foram separados os testes das funções puras e impuras, para facilitar a organização</p>

<h3>Ajuste nas funções de adoção <a href="https://github.com/paygoc6/AuauPI/pull/33">23672befc6f68bdc99f10ea639c15b1b089e6160</a></h3>
<p>Com isso, não á mais possível adotar um cachorro que já esteja como adopted? True, evitando que o mesmo cachorro seja adotado diversas vezes</p>

<h3>Serapação de funções <a href="https://github.com/paygoc6/AuauPI/pull/34">e16f3a6470e0cd092c80fa2e84b39ca2c80304af</a></h3>
<p>Funções que possuem a finalidade de alterar o Atom do bd, foram isoladas no NS específico para BD, para uma melhor organização</p>

<h3>Validação de raças no POST <a href="https://github.com/paygoc6/AuauPI/pull/36">9df7b435d70eaafc7ffacb22255cbcd186e62c9d</a></h3>
<p>Agora, o Atom criado anteriormente para armazenamento de raças é usado para validação na hora do cadastro de um cachorro</p>

<h3>Alteração nos testes <a href="https://github.com/paygoc6/AuauPI/pull/37">fb96abc0654fd614380952877e2271fcb742e106</a></h3>
<p>Todos os testes foram atualizados para funcionarem de acordo com as alterações realizadas no projeto ao longo dos ultimos commits, junto com uma modificação no atom do projeto para se adequar as mesmas</p>

<h3>Alteração nos testes envolvendo BD <a href="https://github.com/paygoc6/AuauPI/pull/38">5f3a0a383739db274fb368e191f133ac05ceb49f</a></h3>
<p>Foi criado um BD próprio para os testes da aplicação, não mais resetando o BD usado na aplicação, pois em um cenário real, o BD não pode ser deletado ou alterado para testes</p>

<h1>Datomic</h1>

<h3>Configuração do datomic <a href="https://github.com/paygoc6/AuauPI/pull/40">b27155beead6312b498fd42038d2e8aa6e7d0cbf</a></h3>
<p>Foi configurado o Datomic do projeto, com suas dependências e feita a criação do BD e de seu Schema</p>

<h3>Criação e configuração do config-map <a href="https://github.com/paygoc6/AuauPI/pull/41">eed5b09f6e5f4471b4abe73983698cc230faab58</a></h3>
<p>Config-map é a configuração de quais dados serão utilizados na API, basicamente um mapa de configurações, como o nome diz</p>

<h3>Atualização na rota GET/dogs <a href="https://github.com/paygoc6/AuauPI/pull/42">f695dd0a726c424e8bf8c9552cb1d30cd9e48764</a></h3>
<p>Agora a função get-dogs-handler não mais requisita os dados de um atom, sendo alterada para utilizar uma query no banco do Datomic para retornar todos os cachorros cadastrados com informações resumidas</p>

<h3>Alteração na get dog by id <a href="https://github.com/paygoc6/AuauPI/pull/43">b177ec0148fcb9f2143b9bb22694d6e9b25ed6f6</a></h3>
<p>Assim como na alteração anterior, a função de buscar um cachorro específico também foi alterada para utilizar querys no datomic</p>

<h3>Carga inicial do datomic <a href="https://github.com/paygoc6/AuauPI/pull/44">4e3afdec41ab5f09e331dca70d8d8b03a55d1e2e</a></h3>
<p>Inseridos os primeiros cachorros no BD do datomic, e atualizados os testes para que validem as informações corretas do mesmo</p>

<h3>Alteração na rota POST de cadastro <a href="https://github.com/paygoc6/AuauPI/pull/45">aef1a81cec73dd41e13fbfceb3669c31d503d346</a></h3>
<p>Novamente uma adequação para utilização do datomic nas rotas, desta vez, para a rota POST de cadastro de cachorros</p>

<h3>Alteração na rota POST de adoção <a href="https://github.com/paygoc6/AuauPI/pull/46">4baa3c76beb4387c7c9a63221b9a9f483a0d708c</a></h3>
<p>Mesma alteração que a anterior, desta vez para a adoção de cachorros utilizando o datomic</p>

<h3>Alteração na core_test <a href="https://github.com/paygoc6/AuauPI/pull/47">4d548426b5dc91625d2defd550ccbe19e2a74155</a></h3>
<p>Alterações nos testes de rota, para utilizarem chamadas no datomic</p>

<h3>Atualização nos filtros de busca <a href="https://github.com/paygoc6/AuauPI/pull/48">2f798e6bfa24b346bb356dbf359e167d9d1b536d</a></h3>
<p>Criação de um "find" no datomic, com a finalidade de utilizar os filtros de atributos criados para as rotas GET anteriormente, (idade, raça, etc)</p>

<h3>Testes com linha de comando <a href="https://github.com/paygoc6/AuauPI/pull/49">a9b057c3deb9e51325383140d1cf71b1148c2959</a></h3>
<p>criado um comando para gerar uma build de testes, onde todos os testes são rodados, e após isso, o programa é encerrado</p>

