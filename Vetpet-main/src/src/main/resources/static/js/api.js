/* Camada de acesso à API privada da clínica (Spring Boot).
   O frontend é servido pelo próprio Spring (src/main/resources/static), portanto a URL base é a mesma origem
   e o cookie de sessão (JSESSIONID) é enviado automaticamente.
   Se o frontend for servido em outra porta, use "http://localhost:8080" e habilite CORS com credenciais no backend. */
const API_BASE_URL = "";

const Api = { onSessaoExpirada: null };

async function apiRequest(path, options = {}) {
  const { silencioso, ...fetchOptions } = options;
  let response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      credentials: "include",
      ...fetchOptions,
      headers: { "Content-Type": "application/json", ...fetchOptions.headers }
    });
  } catch {
    throw new Error("Não foi possível conectar à API da clínica. Verifique se o backend está em execução.");
  }
  if (!response.ok) {
    let mensagem = null;
    try { mensagem = (await response.json())?.mensagem; } catch { /* resposta sem corpo JSON */ }
    if (response.status === 401 && !silencioso && Api.onSessaoExpirada) Api.onSessaoExpirada();
    throw new Error(mensagem || (response.status === 401
      ? "Sessão expirada. Entre novamente."
      : `A API respondeu com status ${response.status}.`));
  }
  return response.status === 204 ? null : response.json();
}

const enviar = (metodo, caminho, corpo, extra = {}) =>
  apiRequest(caminho, { method: metodo, body: corpo === undefined ? undefined : JSON.stringify(corpo), ...extra });

// Consulta pública independente do backend da clínica.
async function buscarCep(cep) {
  const numero = cep.replace(/\D/g, "");
  if (numero.length !== 8) throw new Error("Informe um CEP com 8 números.");

  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), 8000);
  try {
    const resposta = await fetch(`https://viacep.com.br/ws/${numero}/json/`, {
      signal: controller.signal
    });
    if (!resposta.ok) throw new Error("O ViaCEP está indisponível no momento.");
    const dados = await resposta.json();
    if (dados.erro) throw new Error("CEP não encontrado. Confira o número ou preencha o endereço manualmente.");
    return dados;
  } catch (erro) {
    if (erro.name === "AbortError") throw new Error("A consulta ao CEP demorou demais. Tente novamente.");
    throw erro;
  } finally {
    clearTimeout(timeout);
  }
}

/* ---------- Tradução entre valores da tela e valores da API ---------- */
const inverso = mapa => Object.fromEntries(Object.entries(mapa).map(([chave, valor]) => [valor, chave]));
const SITUACAO_CADASTRO_UI = { ATIVO: "Ativo", INATIVO: "Inativo", OBITO: "Óbito" };
const SITUACAO_CADASTRO_API = inverso(SITUACAO_CADASTRO_UI);
const SEXO_UI = { F: "Fêmea", M: "Macho" };
const SEXO_API = inverso(SEXO_UI);
const SITUACAO_CONSULTA_UI = { AGENDADA: "Agendada", REALIZADA: "Realizada", CANCELADA: "Cancelada", NAO_COMPARECEU: "Não compareceu" };
const SITUACAO_CONSULTA_API = inverso(SITUACAO_CONSULTA_UI);
const TIPO_VACINA_UI = { ANTIRRABICA: "Antirrábica", POLIVALENTE: "Polivalente", OUTRA: "Outra" };
const TIPO_VACINA_API = inverso(TIPO_VACINA_UI);
const SITUACAO_VACINA_UI = { AGENDADA: "Agendada", APLICADA: "Aplicada", CANCELADA: "Cancelada" };
const SITUACAO_VACINA_API = inverso(SITUACAO_VACINA_UI);
const SITUACAO_INTEGRACAO_UI = { PENDENTE: "Pendente", ENVIADA: "Enviada", CONFIRMADA: "Confirmada", FALHA: "Falha" };
const OPERACAO_UI = {
  CADASTRO_ANIMAL: "Cadastro de animal", ATUALIZACAO_ANIMAL: "Atualização de animal", VACINA_ANTIRRABICA: "Vacinação antirrábica"
};

const texto = v => (v === null || v === undefined) ? "" : String(v);
const textoOuNulo = v => { const t = texto(v).trim(); return t === "" ? null : t; };
const numeroOuNulo = v => { const t = textoOuNulo(v); return t === null ? null : Number(t.replace(",", ".")); };
const ordenarRecentes = lista => lista.sort((a, b) => Number(b.id) - Number(a.id));

function validarUf(valor, rotulo) {
  const uf = texto(valor).trim().toUpperCase();
  if (!/^[A-Z]{2}$/.test(uf)) throw new Error(`${rotulo} deve ter 2 letras.`);
  return uf;
}
function erroParcial(mensagem) {
  const erro = new Error(mensagem);
  erro.parcial = true;
  return erro;
}

/* ---------- API -> tela ---------- */
function tutorDaApi(t) {
  const e = t.endereco || {};
  return {
    id: texto(t.id), nome: t.nomeCompleto, cpf: t.cpf, nascimento: texto(t.dataNascimento), email: t.email,
    telefone: t.telefone, cep: texto(e.cep), logradouro: texto(e.logradouro), numero: texto(e.numero),
    complemento: texto(e.complemento), bairro: texto(e.bairro), cidade: texto(e.cidade), uf: texto(e.uf),
    ibge: texto(e.codigoIbgeMunicipio), situacao: SITUACAO_CADASTRO_UI[t.situacao] || texto(t.situacao)
  };
}
function veterinarioDaApi(v) {
  return {
    id: texto(v.id), nome: v.nomeCompleto, cpf: v.cpf, crmv: v.numeroCrmv, ufCrmv: v.ufCrmv, email: v.email,
    telefone: v.telefone, senhaConfirmada: v.senhaConfirmada ? "Sim" : "Não",
    situacao: SITUACAO_CADASTRO_UI[v.situacao] || texto(v.situacao)
  };
}
function animalDaApi(a, vinculos, nomesVeterinarios) {
  const ativo = vinculos.find(v => v.ativo);
  return {
    id: texto(a.id), nome: a.nome, tutor: texto(a.tutorId), nascimento: texto(a.dataNascimento),
    idadeEstimada: texto(a.idadeEstimada), especie: texto(a.especie), raca: texto(a.raca),
    sexo: SEXO_UI[a.sexo] || "", cor: texto(a.cor), peso: texto(a.pesoAtual), microchip: ativo ? ativo.identificador : "",
    situacao: SITUACAO_CADASTRO_UI[a.situacao] || texto(a.situacao),
    microchipAtual: ativo ? {
      microchip: ativo.identificador, data: texto(ativo.dataVinculacao).slice(0, 16).replace("T", " ") || "Data não informada",
      veterinario: nomesVeterinarios.get(texto(ativo.veterinarioId)) || "Responsável não informado"
    } : null,
    historicoMicrochips: vinculos.filter(v => !v.ativo).map(v => ({
      microchip: v.identificador, data: texto(v.dataVinculacao).slice(0, 10) || "Data não informada",
      veterinario: nomesVeterinarios.get(texto(v.veterinarioId)) || "Responsável não informado"
    }))
  };
}
function consultaDaApi(c) {
  return {
    id: texto(c.id), animal: texto(c.animalId), veterinario: texto(c.veterinarioId), data: texto(c.dataHora).slice(0, 16),
    motivo: texto(c.motivo), anamnese: texto(c.anamnese), sintomas: texto(c.sintomas), diagnostico: texto(c.diagnostico),
    tratamento: texto(c.tratamentoPrescrito), medicamentos: texto(c.prescricaoMedicamentos), peso: texto(c.pesoRegistrado),
    temperatura: texto(c.temperatura), observacoes: texto(c.observacoes),
    situacao: SITUACAO_CONSULTA_UI[c.situacao] || texto(c.situacao)
  };
}
function vacinaDaApi(v) {
  return {
    id: texto(v.id), animal: texto(v.animalId), veterinario: texto(v.veterinarioId), nome: texto(v.nome),
    tipo: TIPO_VACINA_UI[v.tipo] || texto(v.tipo), fabricante: texto(v.fabricante), lote: texto(v.numeroLote),
    data: texto(v.dataAplicacao), dose: texto(v.doseEtapa), via: texto(v.viaAplicacao), proximaDose: texto(v.dataProximaDose),
    validade: texto(v.dataValidadeLote), observacoes: texto(v.observacao),
    situacao: SITUACAO_VACINA_UI[v.situacao] || texto(v.situacao)
  };
}
function integracaoDaApi(i, nomeAnimal, identificadores) {
  return {
    id: texto(i.id), animal: nomeAnimal, microchip: identificadores.get(i.vinculoMicrochipId) || "Não informado",
    operacao: OPERACAO_UI[i.tipoOperacao] || texto(i.tipoOperacao),
    protocolo: i.protocoloExterno || (i.situacao === "FALHA" ? "Sem protocolo" : "Aguardando resposta"),
    data: texto(i.dataHoraSolicitacao).slice(0, 16), tentativas: texto(i.quantidadeTentativas),
    erro: texto(i.mensagemErro), situacao: SITUACAO_INTEGRACAO_UI[i.situacao] || texto(i.situacao)
  };
}

/* ---------- tela -> API ---------- */
function tutorParaApi(f) {
  const cep = texto(f.cep).replace(/\D/g, "");
  if (cep.length !== 8) throw new Error("CEP deve conter 8 dígitos.");
  const ibge = textoOuNulo(f.ibge);
  if (ibge && !/^\d{7}$/.test(ibge)) throw new Error("O código IBGE deve ter 7 dígitos.");
  return {
    nomeCompleto: texto(f.nome).trim(), cpf: texto(f.cpf).trim(), email: texto(f.email).trim(),
    telefone: texto(f.telefone).trim(), dataNascimento: textoOuNulo(f.nascimento),
    endereco: {
      cep, logradouro: texto(f.logradouro).trim(), numero: texto(f.numero).trim(), complemento: textoOuNulo(f.complemento),
      bairro: texto(f.bairro).trim(), cidade: texto(f.cidade).trim(), uf: validarUf(f.uf, "A UF"), codigoIbgeMunicipio: ibge
    }
  };
}
function veterinarioParaApi(f, criando) {
  const corpo = {
    nomeCompleto: texto(f.nome).trim(), cpf: texto(f.cpf).trim(), email: texto(f.email).trim(),
    telefone: texto(f.telefone).trim(), numeroCrmv: texto(f.crmv).trim(), ufCrmv: validarUf(f.ufCrmv, "A UF do CRMV")
  };
  if (criando) {
    if (texto(f.senha).length < 8) throw new Error("A senha deve ter de 8 a 72 caracteres.");
    corpo.senha = f.senha;
  }
  return corpo;
}
function animalParaApi(f, item, catalogo) {
  const especie = catalogo.especies.find(e => e.nome === f.especie);
  if (!especie) throw new Error("Selecione a espécie.");
  const raca = textoOuNulo(f.raca) && f.raca !== "Sem raça definida"
    ? catalogo.racas.find(r => r.especieId === especie.id && r.nome === f.raca) : null;
  const nascimento = textoOuNulo(f.nascimento);
  const idade = !nascimento && item && textoOuNulo(item.idadeEstimada) !== null ? Number(item.idadeEstimada) : null;
  const microchip = textoOuNulo(f.microchip);
  if (microchip && !/^[A-Za-z0-9]{1,20}$/.test(microchip)) {
    throw new Error("O microchip deve ter de 1 a 20 letras ou números, sem espaços.");
  }
  return {
    tutorId: Number(f.tutor), especieId: Number(especie.id), racaId: raca ? Number(raca.id) : null,
    nome: texto(f.nome).trim(), dataNascimento: nascimento, idadeEstimada: idade, sexo: SEXO_API[f.sexo] || null,
    cor: texto(f.cor).trim(), pesoAtual: numeroOuNulo(f.peso)
  };
}
function consultaParaApi(f) {
  return {
    animalId: Number(f.animal), dataHora: textoOuNulo(f.data), motivo: texto(f.motivo).trim(),
    anamnese: textoOuNulo(f.anamnese), sintomas: textoOuNulo(f.sintomas), diagnostico: textoOuNulo(f.diagnostico),
    tratamentoPrescrito: textoOuNulo(f.tratamento), prescricaoMedicamentos: textoOuNulo(f.medicamentos),
    pesoRegistrado: numeroOuNulo(f.peso), temperatura: numeroOuNulo(f.temperatura), observacoes: textoOuNulo(f.observacoes),
    situacao: SITUACAO_CONSULTA_API[f.situacao] || null
  };
}
function vacinaParaApi(f) {
  return {
    animalId: Number(f.animal), nome: texto(f.nome).trim(), tipo: TIPO_VACINA_API[f.tipo] || null,
    fabricante: texto(f.fabricante).trim(), numeroLote: texto(f.lote).trim(), dataAplicacao: textoOuNulo(f.data),
    doseEtapa: texto(f.dose).trim(), viaAplicacao: texto(f.via).trim(), dataProximaDose: textoOuNulo(f.proximaDose),
    dataValidadeLote: textoOuNulo(f.validade), observacao: textoOuNulo(f.observacoes),
    situacao: SITUACAO_VACINA_API[f.situacao] || null
  };
}

/* ---------- Autenticação ---------- */
Api.login = (email, senha) =>
  enviar("POST", "/api/auth/login", { email: texto(email).trim(), senha }, { silencioso: true });
Api.logout = () => enviar("POST", "/api/auth/logout", undefined, { silencioso: true });
Api.me = () => apiRequest("/api/auth/me", { silencioso: true });
Api.cadastrarPrimeiroVeterinario = (chave, dados) =>
  enviar("POST", "/api/auth/bootstrap", veterinarioParaApi(dados, true),
    { silencioso: true, headers: { "X-Bootstrap-Secret": texto(chave) } });
Api.confirmarSenha = (email, codigo) =>
  enviar("POST", "/api/auth/confirmacao", { email: texto(email).trim(), codigo: texto(codigo).trim() }, { silencioso: true });

/* ---------- Carga dos dados ---------- */
Api.carregarTudo = async function () {
  const [tutores, animais, veterinarios, especies, racas] = await Promise.all([
    apiRequest("/api/tutores"), apiRequest("/api/animais"), apiRequest("/api/veterinarios"),
    apiRequest("/api/especies"), apiRequest("/api/racas")
  ]);
  // A API lista consultas, vacinas, microchips e integrações por animal.
  const detalhes = await Promise.all(animais.map(async animal => {
    const [microchips, consultas, vacinas, integracoes] = await Promise.all([
      apiRequest(`/api/animais/${animal.id}/microchip/historico`),
      apiRequest(`/api/consultas/animal/${animal.id}`),
      apiRequest(`/api/animais/${animal.id}/vacinas`),
      apiRequest(`/api/integracoes/animal/${animal.id}`)
    ]);
    return { animal, microchips, consultas, vacinas, integracoes };
  }));
  const nomesVeterinarios = new Map(veterinarios.map(v => [texto(v.id), v.nomeCompleto]));
  const identificadores = new Map();
  const dados = {
    tutores: ordenarRecentes(tutores.map(tutorDaApi)),
    veterinarios: ordenarRecentes(veterinarios.map(veterinarioDaApi)),
    especies: especies.filter(e => e.situacao === "ATIVO").map(e => ({ id: texto(e.id), nome: e.nome })),
    racas: racas.filter(r => r.situacao === "ATIVO").map(r => ({ id: texto(r.id), especieId: texto(r.especieId), nome: r.nome })),
    animais: [], consultas: [], vacinas: [], integracoes: []
  };
  for (const d of detalhes) {
    d.microchips.forEach(m => identificadores.set(m.id, m.identificador));
    dados.animais.push(animalDaApi(d.animal, d.microchips, nomesVeterinarios));
    dados.consultas.push(...d.consultas.map(consultaDaApi));
    dados.vacinas.push(...d.vacinas.map(vacinaDaApi));
  }
  for (const d of detalhes) {
    dados.integracoes.push(...d.integracoes.map(i => integracaoDaApi(i, d.animal.nome, identificadores)));
  }
  ordenarRecentes(dados.animais); ordenarRecentes(dados.vacinas); ordenarRecentes(dados.integracoes);
  return dados;
};

/* ---------- Gravação ---------- */
// Etapas posteriores ao cadastro principal: se falharem, o registro principal já existe.
async function etapaPosterior(descricao, acao) {
  try { await acao(); } catch (erro) { throw erroParcial(`${descricao} ${erro.message}`); }
}

Api.salvar = async function (kind, f, item, dados) {
  if (kind === "tutores") {
    const corpo = tutorParaApi(f);
    const r = item ? await enviar("PUT", `/api/tutores/${item.id}`, corpo) : await enviar("POST", "/api/tutores", corpo);
    return { id: texto(r.id) };
  }
  if (kind === "animais") {
    const corpo = animalParaApi(f, item, dados);
    const salvo = item ? await enviar("PUT", `/api/animais/${item.id}`, corpo) : await enviar("POST", "/api/animais", corpo);
    const microchip = textoOuNulo(f.microchip);
    if (microchip && microchip !== texto(item?.microchip)) {
      await etapaPosterior("O animal foi salvo, mas o microchip não foi vinculado:",
        () => enviar("POST", `/api/animais/${salvo.id}/microchip`, { identificador: microchip }));
    }
    const situacao = SITUACAO_CADASTRO_API[f.situacao];
    if (situacao && situacao !== salvo.situacao) {
      await etapaPosterior("O animal foi salvo, mas a situação não foi alterada:",
        () => enviar("PATCH", `/api/animais/${salvo.id}/situacao?situacao=${situacao}`));
    }
    return { id: texto(salvo.id) };
  }
  if (kind === "veterinarios") {
    const corpo = veterinarioParaApi(f, !item);
    const r = item ? await enviar("PUT", `/api/veterinarios/${item.id}`, corpo) : await enviar("POST", "/api/veterinarios", corpo);
    const situacao = SITUACAO_CADASTRO_API[f.situacao];
    if (situacao && situacao !== r.situacao) {
      await etapaPosterior("O veterinário foi salvo, mas a situação não foi alterada:",
        () => enviar("PATCH", `/api/veterinarios/${r.id}/situacao?situacao=${situacao}`));
    }
    return { id: texto(r.id), email: r.email, codigoConfirmacao: r.codigoConfirmacao || null, emailEnviado: !item && !r.codigoConfirmacao };
  }
  if (kind === "consultas") {
    const corpo = consultaParaApi(f);
    const r = item ? await enviar("PUT", `/api/consultas/${item.id}`, corpo) : await enviar("POST", "/api/consultas", corpo);
    return { id: texto(r.id) };
  }
  if (kind === "vacinas") {
    if (item) throw new Error("Vacinas registradas só permitem alterar a situação (aplicar ou cancelar).");
    const r = await enviar("POST", "/api/vacinas", vacinaParaApi(f));
    return { id: texto(r.id) };
  }
  throw new Error("Operação não suportada.");
};

Api.alterarSituacaoVacina = (id, situacao, dataAplicacao) =>
  enviar("PATCH", `/api/vacinas/${id}/situacao`, { situacao: SITUACAO_VACINA_API[situacao], dataAplicacao: textoOuNulo(dataAplicacao) });

Api.inativar = function (kind, id) {
  if (kind === "tutores") return enviar("DELETE", `/api/tutores/${id}`);
  if (kind === "animais") return enviar("PATCH", `/api/animais/${id}/situacao?situacao=INATIVO`);
  if (kind === "veterinarios") return enviar("PATCH", `/api/veterinarios/${id}/situacao?situacao=INATIVO`);
  throw new Error("Operação não suportada.");
};

/* ---------- Integração governamental ---------- */
Api.tentarNovamente = id => enviar("POST", `/api/integracoes/${id}/tentativas`);
Api.reprocessarPendentes = async function (integracoes) {
  const alvo = integracoes.filter(i => i.situacao === "Pendente" || i.situacao === "Falha");
  let confirmadas = 0;
  for (const integracao of alvo) {
    const resultado = await Api.tentarNovamente(integracao.id);
    if (resultado.situacao === "CONFIRMADA") confirmadas++;
  }
  return { total: alvo.length, confirmadas };
};
