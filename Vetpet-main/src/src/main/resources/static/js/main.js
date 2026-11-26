/* Interface integrada à API da clínica (js/api.js). Os dados vêm do backend (MySQL) e a sessão é mantida
   pelo cookie de sessão do Spring Security. Autenticação, validações de negócio e persistência ficam no backend. */
let usuario = null;
const definitions = {
  dashboard: { title: "Visão geral", singular: "", plural: "", icon: "home" },
  tutores: { title: "Tutores", singular: "tutor", plural: "tutores", icon: "users" },
  animais: { title: "Animais", singular: "animal", plural: "animais", icon: "paw" },
  veterinarios: { title: "Veterinários", singular: "veterinário", plural: "veterinários", icon: "stethoscope" },
  consultas: { title: "Consultas", singular: "consulta", plural: "consultas", icon: "calendar" },
  vacinas: { title: "Vacinas", singular: "vacina", plural: "vacinas", icon: "syringe" },
  integracoes: { title: "Integrações", singular: "integração", plural: "integrações", icon: "plug" }
};
const iconPaths = {
  home: '<path d="m3 10 9-7 9 7v10a1 1 0 0 1-1 1h-5v-6H9v6H4a1 1 0 0 1-1-1z"/>',
  users: '<path d="M16 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="10" cy="7" r="4"/><path d="M20 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>',
  paw: '<circle cx="7.5" cy="8" r="1.7"/><circle cx="16.5" cy="8" r="1.7"/><circle cx="5" cy="13" r="1.7"/><circle cx="19" cy="13" r="1.7"/><path d="M12 12c-2.9 0-5.6 4.6-4.1 6.8 1.1 1.6 2.4.4 4.1.4s3 .1 4.1-.4C17.6 16.6 14.9 12 12 12Z"/>',
  brand: '<path d="M6.4 13Q4.9 8.8 4.8 5.3q-.1-1.1 1-.6l6.4 4.1a11.4 11.4 0 0 1 7.6 0l6.4-4.1q1.1-.5 1 .6-.1 3.5-1.6 7.7a10.8 10.8 0 0 1 1.3 5.2c0 5.4-4.9 9-10.9 9s-10.9-3.6-10.9-9a10.8 10.8 0 0 1 1.3-5.2Z" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><path d="m7.4 10.8-1-3.4m18.2 3.4 1-3.4" fill="none" stroke="currentColor" stroke-width="1.4" stroke-linecap="round"/><circle cx="11.7" cy="14.5" r=".65" fill="currentColor" stroke="none"/><circle cx="20.3" cy="14.5" r=".65" fill="currentColor" stroke="none"/><path d="M12.8 18.3h1l.8-.9 1.2 2 1.2-2 .8.9h1.4" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>',
  stethoscope: '<path d="M6 3v5a6 6 0 0 0 12 0V3"/><path d="M6 3H4v3M18 3h2v3M12 14v2a5 5 0 0 0 10 0v-1"/><circle cx="22" cy="13" r="2"/>',
  calendar: '<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 11h18M8 15h3"/>',
  syringe: '<path d="m18 2 4 4M17 7l3-3M3 21l9-9M7 17l-4-4M14 4l6 6M11 7l6 6M5 19l-2 2"/>',
  plug: '<path d="M12 22v-5M9 8V2M15 8V2M7 8h10v4a5 5 0 0 1-10 0z"/>',
  plus: '<path d="M12 5v14M5 12h14"/>',
  search: '<circle cx="11" cy="11" r="7"/><path d="m20 20-4-4"/>',
  filter: '<path d="M4 7h16M7 12h10M10 17h4"/><circle cx="8" cy="7" r="1.5"/><circle cx="15" cy="12" r="1.5"/><circle cx="12" cy="17" r="1.5"/>',
  eye: '<path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7Z"/><circle cx="12" cy="12" r="3"/>',
  edit: '<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L8 18l-4 1 1-4Z"/>',
  more: '<circle cx="5" cy="12" r="1"/><circle cx="12" cy="12" r="1"/><circle cx="19" cy="12" r="1"/>',
  arrow: '<path d="M5 12h14M13 6l6 6-6 6"/>',
  refresh: '<path d="M20 7v5h-5M4 17v-5h5"/><path d="M5.5 9a7 7 0 0 1 11.6-2L20 12M4 12l2.9 5a7 7 0 0 0 11.6-2"/>',
  info: '<circle cx="12" cy="12" r="10"/><path d="M12 11v5M12 8h.01"/>',
  close: '<path d="m18 6-12 12M6 6l12 12"/>',
  pin: '<path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/>',
  shield: '<path d="M12 22s8-4 8-11V5l-8-3-8 3v6c0 7 8 11 8 11Z"/><path d="m9 12 2 2 4-4"/>',
  clock: '<circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>',
  alert: '<circle cx="12" cy="12" r="10"/><path d="M12 8v5M12 16h.01"/>',
  tag: '<path d="M20.6 13.4 13.4 20.6a2 2 0 0 1-2.8 0L3 13V3h10l7.6 7.6a2 2 0 0 1 0 2.8Z"/><circle cx="7.5" cy="7.5" r="1.5"/>',
  layers: '<path d="m12 3 9 5-9 5-9-5 9-5Z"/><path d="m3 12 9 5 9-5M3 16l9 5 9-5"/>',
  clipboard: '<rect x="5" y="4" width="14" height="17" rx="2"/><path d="M9 4.5h6V7H9zM9 12h6M9 16h6"/>',
  logout: '<path d="M10 17l5-5-5-5M15 12H3"/><path d="M12 3h6a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-6"/>'
};
function icon(name, className = "ui-icon") {
  const viewBox = name === "brand" ? "0 0 32 32" : "0 0 24 24";
  return `<svg class="${className}" viewBox="${viewBox}" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" focusable="false">${iconPaths[name] || iconPaths.info}</svg>`;
}
const situacaoPadrao = { tutores: "Ativo", animais: "Ativo", veterinarios: "Ativo", consultas: "Agendada", vacinas: "Aplicada" };
const fields = {
  tutores: [
    ["nome", "Nome completo", "text", true], ["cpf", "CPF", "text", true],
    ["nascimento", "Data de nascimento", "date", true], ["email", "E-mail", "email", true],
    ["telefone", "Telefone", "tel", true], ["cep", "CEP", "text", true, "cep"],
    ["logradouro", "Logradouro", "text", true], ["numero", "Número", "text", true],
    ["complemento", "Complemento", "text"], ["bairro", "Bairro", "text", true],
    ["cidade", "Cidade", "text", true], ["uf", "UF", "text", true], ["ibge", "Código IBGE", "text"]
  ],
  animais: [
    ["nome", "Nome do animal", "text", true], ["tutor", "Tutor responsável", "select", true, "tutores"],
    ["nascimento", "Data de nascimento", "date"], ["especie", "Espécie", "select", true, "especies"],
    ["raca", "Raça", "select", false, "racas"], ["sexo", "Sexo", "select", true, ["Fêmea", "Macho"]],
    ["cor", "Cor", "text", true], ["peso", "Peso atual (kg)", "number", true],
    ["microchip", "Microchip RFID", "text", true],
    ["situacao", "Situação", "select", false, ["Ativo", "Inativo", "Óbito"]]
  ],
  veterinarios: [
    ["nome", "Nome completo", "text", true], ["cpf", "CPF", "text", true],
    ["crmv", "Número CRMV", "text", true], ["ufCrmv", "UF do CRMV", "text", true],
    ["email", "E-mail", "email", true], ["telefone", "Telefone", "tel", true],
    ["senha", "Senha de acesso (mínimo 8 caracteres)", "password", true],
    ["situacao", "Situação", "select", false, ["Ativo", "Inativo"]]
  ],
  consultas: [
    ["animal", "Animal", "select", true, "animais"],
    ["data", "Data e hora", "datetime-local", true], ["motivo", "Motivo", "text", true],
    ["anamnese", "Anamnese", "textarea"], ["sintomas", "Sintomas", "textarea"],
    ["diagnostico", "Diagnóstico", "textarea"], ["tratamento", "Tratamento prescrito", "textarea"],
    ["medicamentos", "Prescrição de medicamentos", "textarea"], ["peso", "Peso registrado (kg)", "number"],
    ["temperatura", "Temperatura (°C)", "number"], ["observacoes", "Observações", "textarea"],
    ["situacao", "Situação", "select", false, ["Agendada", "Realizada", "Cancelada", "Não compareceu"]]
  ],
  vacinas: [
    ["animal", "Animal", "select", true, "animais"],
    ["nome", "Nome da vacina", "text", true], ["tipo", "Classificação", "select", true, ["Antirrábica", "Polivalente", "Outra"]],
    ["fabricante", "Fabricante", "text", true], ["lote", "Número do lote", "text", true],
    ["data", "Data de aplicação", "date"], ["dose", "Dose ou etapa", "text", true],
    ["via", "Via de aplicação", "text", true], ["proximaDose", "Próxima dose", "date"],
    ["validade", "Validade do lote", "date", true], ["observacoes", "Observações", "textarea"],
    ["situacao", "Situação", "select", false, ["Aplicada", "Agendada", "Cancelada"]]
  ]
};
function camposDe(kind, item) {
  return fields[kind].filter(field => !(kind === "veterinarios" && field[0] === "senha" && item));
}
function hojeLocal() {
  const agora = new Date();
  return new Date(agora.getTime() - agora.getTimezoneOffset() * 60000).toISOString().slice(0, 10);
}
const columns = {
  tutores: [["nome", "Nome"], ["cpf", "CPF"], ["telefone", "Telefone"], ["email", "E-mail"], ["situacao", "Situação"]],
  animais: [["nome", "Animal"], ["especie", "Espécie"], ["tutor", "Tutor"], ["microchip", "Microchip RFID"], ["situacao", "Situação"]],
  veterinarios: [["nome", "Nome"], ["crmv", "CRMV"], ["email", "E-mail"], ["senhaConfirmada", "Senha confirmada"], ["situacao", "Situação"]],
  consultas: [["animal", "Animal"], ["veterinario", "Veterinário"], ["data", "Data e hora"], ["motivo", "Motivo"], ["situacao", "Situação"]],
  vacinas: [["animal", "Animal"], ["nome", "Vacina"], ["tipo", "Classificação"], ["data", "Aplicação"], ["proximaDose", "Próxima dose"], ["situacao", "Situação"]],
  integracoes: [["animal", "Animal"], ["microchip", "Microchip"], ["operacao", "Operação"], ["protocolo", "Protocolo"], ["data", "Solicitação"], ["situacao", "Situação"]]
};
let store = {};
let tutorSelecionadoId = null;
const escapeHtml = (value = "") => String(value).replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
function valor(data, key) {
  const item = data[key];
  if (key === "animal" || key === "tutor" || key === "veterinario") {
    const kind = key === "animal" ? "animais" : key === "tutor" ? "tutores" : "veterinarios";
    return store[kind]?.find(x => x.id === item)?.nome || item || "—";
  }
  if (key === "data" || key === "proximaDose") return item ? item.replace("T", " ") : "—";
  return item || "—";
}
function selectOptions(source, key) {
  let options = [];
  if (Array.isArray(source)) options = source;
  else if (source === "especies") options = (store.especies || []).map(especie => especie.nome);
  else if (source === "racas") options = ["Sem raça definida"];
  else options = (store[source] || []).filter(row => row.situacao !== "Inativo").map(row => ({ value: row.id, label: row.nome }));
  return `<option value="">Selecione</option>` + options.map(item => {
    const option = typeof item === "string" ? { value: item, label: item } : item;
    return `<option value="${escapeHtml(option.value)}">${escapeHtml(option.label)}</option>`;
  }).join("");
}
function fieldHtml(item, current = "") {
  const [name, label, type, required, source] = item;
  const req = required ? "required" : "";
  const maxLen = { uf: 2, ufCrmv: 2, numero: 10, crmv: 10, dose: 30, via: 30, lote: 40, cor: 40, telefone: 20, complemento: 80, bairro: 80, cidade: 80, motivo: 255 };
  if (type === "select") return `<label class="field"><span>${label}${required ? " *" : ""}</span><select name="${name}" ${req}>${selectOptions(source, name)}</select></label>`;
  if (type === "textarea") return `<label class="field field-wide"><span>${label}</span><textarea name="${name}" rows="3">${escapeHtml(current)}</textarea></label>`;
  const cep = source === "cep" ? `<small class="field-note" data-cep-message>Digite o CEP para preencher o endereço.</small>` : "";
  return `<label class="field ${name === "ibge" ? "" : ""}"><span>${label}${required ? " *" : ""}</span><input name="${name}" type="${type}" value="${escapeHtml(current)}" ${req} ${type === "number" ? `step="${name === "temperatura" ? "0.1" : "0.01"}" min="0"` : ""} ${source === "cep" ? 'inputmode="numeric" maxlength="9"' : ""} ${maxLen[name] ? `maxlength="${maxLen[name]}"` : ""} ${type === "password" ? 'minlength="8" maxlength="72" autocomplete="new-password"' : ""}>${cep}</label>`;
}
function renderLogin(mensagem = "") {
  document.querySelector("#app").innerHTML = `<main class="login-page"><section class="login-card"><div class="brand-mark">${icon("brand")}</div><p class="eyebrow">GESTÃO VETERINÁRIA</p><h1>Bem-vindo à VetPet</h1><p class="muted">Acesse a plataforma para gerenciar a clínica.</p><form id="login-form"><label class="field"><span>E-mail</span><input type="email" name="email" placeholder="voce@clinica.com.br" required></label><label class="field"><span>Senha</span><input type="password" name="senha" placeholder="Sua senha" required></label><p class="login-error" id="login-error">${escapeHtml(mensagem)}</p><button class="button button-primary full" type="submit">Entrar na plataforma ${icon("arrow")}</button><button class="button button-outline full" type="button" id="primeiro-acesso">Primeiro acesso da clínica</button></form></section><p class="login-footer">VetPet · Gestão veterinária</p></main>`;
  document.querySelector("#primeiro-acesso").addEventListener("click", renderPrimeiroAcesso);
  document.querySelector("#login-form").addEventListener("submit", async event => {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    const erro = document.querySelector("#login-error");
    const botao = event.currentTarget.querySelector('button[type="submit"]');
    if (!form.get("email") || !form.get("senha")) return;
    erro.textContent = ""; botao.disabled = true;
    try {
      await Api.login(form.get("email"), form.get("senha"));
      usuario = await Api.me();
      await recarregar();
      render();
    } catch (falha) {
      usuario = null;
      erro.textContent = falha.message;
      botao.disabled = false;
    }
  });
}
function renderPrimeiroAcesso() {
  const campos = [["nome", "Nome completo", "text"], ["cpf", "CPF", "text"], ["email", "E-mail", "email"],
    ["telefone", "Telefone", "tel"], ["crmv", "Número CRMV", "text"], ["ufCrmv", "UF do CRMV", "text"],
    ["senha", "Senha (mínimo 8 caracteres)", "password"], ["confirmarSenha", "Confirmar senha", "password"],
    ["chave", "Chave de instalação", "password"]];
  document.querySelector("#app").innerHTML = `<main class="login-page"><section class="login-card" style="width:min(640px,100%)"><div class="brand-mark">${icon("brand")}</div><p class="eyebrow">PRIMEIRO ACESSO</p><h1>Cadastrar o primeiro veterinário</h1><p class="muted">Disponível somente enquanto a clínica não tiver veterinários cadastrados. Os demais profissionais são cadastrados depois, já dentro do sistema.</p><form id="primeiro-form"><div class="form-grid">${campos.map(([name, label, type]) => `<label class="field"><span>${label} *</span><input name="${name}" type="${type}" required ${name === "ufCrmv" ? 'maxlength="2"' : ""} ${name === "crmv" ? 'maxlength="10"' : ""} ${type === "password" ? 'maxlength="72"' : ""} ${name === "senha" || name === "confirmarSenha" ? 'minlength="8" autocomplete="new-password"' : ""}></label>`).join("")}</div><p class="login-error" id="primeiro-error"></p><button class="button button-primary full" type="submit">Cadastrar veterinário ${icon("arrow")}</button><button class="button button-outline full" type="button" id="voltar-login">Voltar ao login</button></form></section></main>`;
  document.querySelector("#voltar-login").addEventListener("click", () => renderLogin());
  document.querySelector("#primeiro-form").addEventListener("submit", async event => {
    event.preventDefault();
    const dados = Object.fromEntries(new FormData(event.currentTarget).entries());
    const erro = document.querySelector("#primeiro-error");
    const botao = event.currentTarget.querySelector('button[type="submit"]');
    erro.textContent = "";
    if (dados.senha !== dados.confirmarSenha) { erro.textContent = "As senhas não conferem."; return; }
    botao.disabled = true;
    try {
      const resposta = await Api.cadastrarPrimeiroVeterinario(dados.chave, dados);
      renderConfirmacaoInicial(resposta, dados.senha);
    } catch (falha) {
      erro.textContent = falha.message;
      botao.disabled = false;
    }
  });
}
function renderConfirmacaoInicial(resposta, senha) {
  document.querySelector("#app").innerHTML = `<main class="login-page"><section class="login-card"><div class="brand-mark">${icon("brand")}</div><p class="eyebrow">CONFIRMAÇÃO DE SENHA</p><h1>Confirme o seu acesso</h1><p class="muted">${resposta.codigoConfirmacao ? `Simulação do envio por e-mail: o código abaixo seria enviado para ${escapeHtml(resposta.email)} e expira em 20 minutos.` : `Enviamos um código de 6 dígitos para ${escapeHtml(resposta.email)}. Digite-o abaixo; ele expira em 20 minutos.`}</p><form id="confirmacao-form"><label class="field"><span>Código de confirmação</span><input name="codigo" value="${escapeHtml(resposta.codigoConfirmacao || "")}" required inputmode="numeric" maxlength="6"></label><p class="login-error" id="confirmacao-error"></p><button class="button button-primary full" type="submit">Confirmar e entrar ${icon("arrow")}</button></form></section></main>`;
  document.querySelector("#confirmacao-form").addEventListener("submit", async event => {
    event.preventDefault();
    const codigo = new FormData(event.currentTarget).get("codigo");
    const erro = document.querySelector("#confirmacao-error");
    const botao = event.currentTarget.querySelector('button[type="submit"]');
    erro.textContent = ""; botao.disabled = true;
    try {
      await Api.confirmarSenha(resposta.email, codigo);
      await Api.login(resposta.email, senha);
      usuario = await Api.me();
      await recarregar();
      render();
    } catch (falha) {
      erro.textContent = falha.message;
      botao.disabled = false;
    }
  });
}
function navLink(key, active) {
  return `<a href="#${key}" class="nav-link ${active === key ? "active" : ""}"><span class="nav-icon">${icon(definitions[key].icon)}</span>${definitions[key].title}</a>`;
}
function renderShell(page) {
  const user = usuario?.nomeCompleto || usuario?.email || "Veterinário";
  document.querySelector("#app").innerHTML = `<div class="app-shell"><aside class="sidebar"><a class="brand" href="#dashboard"><span class="brand-mark small">${icon("brand")}</span><span>VetPet<small>CLÍNICA VETERINÁRIA</small></span></a><div class="nav-caption">MENU PRINCIPAL</div><nav>${["dashboard", "tutores", "animais", "veterinarios", "consultas", "vacinas", "integracoes"].map(key => navLink(key, page)).join("")}</nav><div class="sidebar-bottom"><div class="profile"><span class="avatar">${escapeHtml(user[0]?.toUpperCase() || "V")}</span><span class="profile-text"><strong>Veterinário</strong><small>${escapeHtml(user)}</small></span></div><button class="logout-button" id="logout" type="button">${icon("logout")}<span>Sair</span></button></div></aside><main class="main-area"><header class="topbar"><div><span class="breadcrumb">Clínica <span>/</span> ${definitions[page].title}</span><h1>${definitions[page].title}</h1></div><div class="top-actions"><span class="today">${new Intl.DateTimeFormat("pt-BR", { dateStyle: "medium" }).format(new Date())}</span></div></header><div class="page-content" id="page-content"></div></main></div>`;
  document.querySelector("#logout").addEventListener("click", async () => { try { await Api.logout(); } catch { /* sessão já encerrada */ } usuario = null; store = {}; render(); });
}
function metric(iconMarkup, label, count, color) {
  return `<article class="metric-card"><span class="metric-icon ${color}">${iconMarkup}</span><div><p>${label}</p><strong>${count}</strong><small>Total cadastrado</small></div><span class="metric-trend">${icon("arrow")}</span></article>`;
}
function renderDashboard() {
  const counts = { tutores: (store.tutores || []).length, animais: (store.animais || []).length, consultas: (store.consultas || []).length, vacinas: (store.vacinas || []).filter(row => row.situacao === "Aplicada").length };
  const content = document.querySelector("#page-content");
  content.innerHTML = `<section class="welcome-banner"><div><span class="eyebrow">RESUMO DA CLÍNICA</span><h2>Cuidado que conecta.<br><em>Gestão que simplifica.</em></h2><p>Acompanhe os cadastros e atendimentos da sua clínica em um só lugar.</p><a href="#tutores" class="button button-light">Acessar tutores ${icon("arrow")}</a></div><div class="banner-art"><span class="art-circle"></span><span class="art-brand">${icon("brand")}</span></div></section><div class="section-heading"><div><span class="eyebrow">SEU ESPAÇO</span><h2>Resumo geral</h2></div><span class="soft-label">Atualizado agora</span></div><section class="metrics-grid">${metric(icon("users"), "Tutores cadastrados", counts.tutores, "mint")}${metric(icon("paw"), "Animais", counts.animais, "peach")}${metric(icon("calendar"), "Consultas", counts.consultas, "lavender")}${metric(icon("syringe"), "Vacinas aplicadas", counts.vacinas, "blue")}</section><section class="lower-grid"><article class="panel"><div class="panel-heading"><div><span class="eyebrow">ACESSO RÁPIDO</span><h3>O que você precisa fazer?</h3></div><span class="panel-dot">···</span></div><div class="shortcut-grid"><a href="#tutores"><span class="shortcut-icon mint">${icon("users")}</span><span><strong>Novo tutor</strong><small>Cadastre um responsável</small></span>${icon("arrow")}</a><a href="#animais"><span class="shortcut-icon peach">${icon("paw")}</span><span><strong>Novo animal</strong><small>Adicione à sua clínica</small></span>${icon("arrow")}</a><a href="#consultas"><span class="shortcut-icon lavender">${icon("calendar")}</span><span><strong>Registrar consulta</strong><small>Atualize o histórico</small></span>${icon("arrow")}</a><a href="#vacinas"><span class="shortcut-icon blue">${icon("syringe")}</span><span><strong>Registrar vacina</strong><small>Controle a vacinação</small></span>${icon("arrow")}</a></div></article><article class="panel integrations-panel"><div class="panel-heading"><div><span class="eyebrow">INTEGRAÇÕES</span><h3>Serviços externos</h3></div><a href="#integracoes" class="text-link">Ver detalhes ${icon("arrow")}</a></div><div class="integration-item"><span class="service-icon cep-icon">${icon("pin")}</span><div><strong>Consulta de CEP</strong><small>ViaCEP · API pública</small></div><span class="status-dot success"></span></div><div class="integration-item"><span class="service-icon gov-icon">${icon("shield")}</span><div><strong>Serviço governamental</strong><small>API governamental simulada</small></div><span class="status-label">${(store.integracoes || []).filter(row => row.situacao === "Pendente" || row.situacao === "Falha").length} pendente(s)</span></div><div class="integration-foot"><span>API da clínica conectada</span>${icon("plug")}</div></article></section>`;
}
function statusClass(status = "") {
  const normalized = status.toLowerCase();
  if (normalized.includes("confirm") || normalized === "ativo" || normalized === "realizada" || normalized === "aplicada" || normalized === "sim") return "confirmed";
  if (normalized.includes("falha") || normalized.includes("cancel") || normalized.includes("inativo")) return "failed";
  if (normalized.includes("pendente") || normalized.includes("agendada") || normalized.includes("enviada")) return "pending";
  return "neutral";
}
function badge(text) { return `<span class="badge ${statusClass(text)}"><i></i>${escapeHtml(text || "—")}</span>`; }
function renderTable(kind, rows, cols) {
  if (!rows.length) return `<div class="empty-state">${icon("clipboard")}<strong>Nenhum registro por aqui ainda</strong><p>Quando você adicionar ${definitions[kind].plural || "um item"}, ele aparecerá nesta lista.</p></div>`;
  return `<div class="table-wrap"><table><thead><tr>${cols.map(([, title]) => `<th>${title}</th>`).join("")}<th></th></tr></thead><tbody>${rows.map(row => `<tr>${cols.map(([key]) => `<td>${key === "situacao" ? badge(valor(row, key)) : key === "tipo" && row[key] === "Antirrábica" ? `<span class="badge rabies"><i></i>Antirrábica</span>` : escapeHtml(valor(row, key))}</td>`).join("")}<td><div class="row-actions"><button data-action="view" data-id="${row.id}" title="Consultar">${icon("eye")}</button>${kind === "integracoes" && (row.situacao === "Pendente" || row.situacao === "Falha") ? `<button data-action="retry" data-id="${row.id}" title="Tentar novamente">${icon("refresh")}</button>` : ""}${kind !== "integracoes" ? `<button data-action="edit" data-id="${row.id}" title="Editar">${icon("edit")}</button>` : ""}${["tutores", "animais", "veterinarios"].includes(kind) ? `<button data-action="deactivate" data-id="${row.id}" title="Inativar">${icon("more")}</button>` : ""}</div></td></tr>`).join("")}</tbody></table></div>`;
}
function cardActions(id, includeDeactivate = false) {
  return `<div class="record-actions"><button class="button button-outline" data-action="view" data-id="${id}">${icon("eye")}Ver detalhes</button><button class="icon-button" data-action="edit" data-id="${id}" title="Editar">${icon("edit")}</button>${includeDeactivate ? `<button class="icon-button" data-action="deactivate" data-id="${id}" title="Inativar">${icon("more")}</button>` : ""}</div>`;
}
function renderAnimals(rows) {
  if (!rows.length) return renderEmptyCollection("animais");
  return `<div class="animal-card-grid">${rows.map(animal => `<article class="animal-card"><div class="animal-card-top"><span class="animal-avatar">${icon("paw")}</span>${badge(animal.situacao || "Ativo")}</div><h3>${escapeHtml(animal.nome || "Animal sem nome")}</h3><p class="animal-breed">${escapeHtml(animal.especie || "Espécie não informada")} · ${escapeHtml(animal.raca || "Sem raça definida")}</p><div class="animal-card-info"><span><small>TUTOR</small><strong>${escapeHtml(valor(animal, "tutor"))}</strong></span><span><small>MICROCHIP RFID</small><strong>${escapeHtml(animal.microchip || "Não informado")}</strong></span></div>${cardActions(animal.id, true)}</article>`).join("")}</div>`;
}
function renderTutors(rows) {
  const selected = rows.find(person => person.id === tutorSelecionadoId) || rows[0];
  if (selected) tutorSelecionadoId = selected.id;
  return `<div class="tutor-workspace"><aside class="tutor-index"><div class="tutor-index-heading"><strong>Responsáveis</strong><span>${rows.length}</span></div><label class="search-box tutor-search">${icon("search")}<input id="tutor-search" type="search" placeholder="Buscar tutor"></label><div id="tutor-results">${renderTutorResults(rows, selected?.id)}</div></aside><section id="tutor-profile" class="tutor-profile">${renderTutorProfile(selected)}</section></div>`;
}
function renderTutorResults(rows, selectedId) {
  if (!rows.length) return `<div class="tutor-list-empty">Nenhum tutor encontrado.</div>`;
  return rows.map(person => `<button class="tutor-list-item ${person.id === selectedId ? "selected" : ""}" data-action="select-tutor" data-id="${person.id}"><span class="tutor-avatar">${escapeHtml((person.nome || "T").trim()[0].toUpperCase())}</span><span><strong>${escapeHtml(person.nome || "Tutor")}</strong><small>${escapeHtml(person.email || person.telefone || "Sem contato")}</small></span>${person.id === selectedId ? `<i>›</i>` : ""}</button>`).join("");
}
function renderTutorProfile(person) {
  if (!person) return `<div class="tutor-profile-empty"><span>${icon("users")}</span><h3>Selecione um tutor</h3><p>Os dados do responsável aparecerão neste painel.</p><button class="button button-primary" data-action="new">${icon("plus")}Cadastrar tutor</button></div>`;
  const pets = (store.animais || []).filter(animal => animal.tutor === person.id);
  return `<div class="tutor-profile-header"><span class="tutor-profile-avatar">${escapeHtml((person.nome || "T").trim()[0].toUpperCase())}</span><div><span class="eyebrow">FICHA DO RESPONSÁVEL</span><h2>${escapeHtml(person.nome || "Tutor")}</h2>${badge(person.situacao || "Ativo")}</div><div class="tutor-profile-actions"><button class="icon-button" data-action="edit" data-id="${person.id}" title="Editar">${icon("edit")}</button><button class="icon-button" data-action="deactivate" data-id="${person.id}" title="Inativar">${icon("more")}</button></div></div><div class="tutor-profile-details"><div><small>CPF</small><strong>${escapeHtml(person.cpf || "Não informado")}</strong></div><div><small>NASCIMENTO</small><strong>${escapeHtml(person.nascimento || "Não informado")}</strong></div><div><small>E-MAIL</small><strong>${escapeHtml(person.email || "Não informado")}</strong></div><div><small>TELEFONE</small><strong>${escapeHtml(person.telefone || "Não informado")}</strong></div><div class="address-detail"><small>ENDEREÇO</small><strong>${escapeHtml([person.logradouro, person.numero, person.bairro, person.cidade, person.uf].filter(Boolean).join(", ") || "Não informado")}</strong></div></div><div class="tutor-pets"><div><span class="eyebrow">ANIMAIS VINCULADOS</span><strong>${pets.length}</strong></div>${pets.length ? pets.map(pet => `<a href="#animais"><span>${icon("paw")}</span><strong>${escapeHtml(pet.nome || "Animal")}</strong><small>${escapeHtml(pet.especie || "Espécie")}${pet.microchip ? ` · RFID ${escapeHtml(pet.microchip)}` : ""}</small></a>`).join("") : `<p>Este tutor ainda não possui animais associados.</p>`}</div>`;
}
function renderVeterinarians(rows) {
  if (!rows.length) return renderEmptyCollection("veterinarios");
  return `<div class="veterinary-roster">${rows.map(person => `<article class="vet-row"><span class="staff-avatar">${escapeHtml((person.nome || "V").split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase())}</span><div class="vet-name"><h3>${escapeHtml(person.nome || "Veterinário")}</h3><span>${escapeHtml(person.email || "E-mail não informado")}</span></div><div class="vet-license"><small>REGISTRO PROFISSIONAL</small><strong>${escapeHtml(person.crmv ? `CRMV ${person.crmv} / ${person.ufCrmv || "—"}` : "Não informado")}</strong></div><div class="vet-access"><small>ACESSO</small>${badge(person.situacao || "Ativo")}<span class="confirmation-tag ${person.senhaConfirmada === "Sim" ? "is-confirmed" : ""}">${person.senhaConfirmada === "Sim" ? `${icon("shield")}Senha confirmada` : "Senha pendente"}</span></div><div class="vet-row-actions">${cardActions(person.id, true)}</div></article>`).join("")}</div>`;
}
function renderConsultations(rows) {
  if (!rows.length) return renderEmptyCollection("consultas");
  const grouped = new Map();
  [...rows].sort((a, b) => (a.data || "").localeCompare(b.data || "")).forEach(visit => {
    const date = (visit.data || "").slice(0, 10) || "Data não definida";
    if (!grouped.has(date)) grouped.set(date, []);
    grouped.get(date).push(visit);
  });
  return `<div class="agenda-board">${[...grouped.entries()].map(([date, visits]) => {
    const dateLabel = date === "Data não definida" ? date : new Intl.DateTimeFormat("pt-BR", { weekday: "long", day: "numeric", month: "long" }).format(new Date(`${date}T12:00:00`));
    return `<section class="agenda-day"><h3 class="agenda-day-heading"><span>${escapeHtml(dateLabel)}</span><small>${visits.length} ${visits.length === 1 ? "consulta" : "consultas"}</small></h3><div class="appointment-list">${visits.map(visit => `<article class="appointment-card"><div class="appointment-time"><strong>${escapeHtml((visit.data || "").slice(11, 16) || "—:—")}</strong><span></span></div><div class="appointment-main"><div class="appointment-heading"><h3>${escapeHtml(valor(visit, "animal"))}</h3>${badge(visit.situacao || "Agendada")}</div><p>${escapeHtml(visit.motivo || "Motivo não informado")}</p><small>Dr(a). ${escapeHtml(valor(visit, "veterinario"))}</small></div><div class="appointment-action"><button class="icon-button" data-action="view" data-id="${visit.id}" title="Consultar">${icon("eye")}</button><button class="icon-button" data-action="edit" data-id="${visit.id}" title="Editar">${icon("edit")}</button></div></article>`).join("")}</div></section>`;
  }).join("")}</div>`;
}
function renderVaccines(rows) {
  if (!rows.length) return renderEmptyCollection("vacinas");
  return `<div class="vaccine-board">${rows.map(dose => `<article class="vaccine-card ${dose.tipo === "Antirrábica" ? "is-rabies" : ""}"><div class="vaccine-date"><span>APLICAÇÃO</span><strong>${escapeHtml(dose.data || "—")}</strong><small>${escapeHtml(dose.situacao || "Agendada")}</small></div><div class="vaccine-details"><div class="vaccine-title-line"><h3>${escapeHtml(dose.nome || "Vacina")}</h3>${dose.tipo === "Antirrábica" ? `<span class="badge rabies"><i></i>Antirrábica</span>` : `<span class="badge neutral"><i></i>Outra vacina</span>`}</div><p>${escapeHtml(valor(dose, "animal"))} <span>·</span> ${escapeHtml(valor(dose, "veterinario"))}</p><div class="vaccine-meta"><span>Lote <strong>${escapeHtml(dose.lote || "—")}</strong></span><span>Fabricante <strong>${escapeHtml(dose.fabricante || "—")}</strong></span></div></div><div class="vaccine-next"><small>PRÓXIMA DOSE</small><strong>${escapeHtml(dose.proximaDose || "Não prevista")}</strong></div><div class="vaccine-actions"><button class="icon-button" data-action="view" data-id="${dose.id}" title="Consultar">${icon("eye")}</button><button class="icon-button" data-action="edit" data-id="${dose.id}" title="Editar">${icon("edit")}</button></div></article>`).join("")}</div>`;
}
function renderUniqueSummary(kind, rows) {
  let cards = [];
  if (kind === "tutores") cards = [["users", "Tutores ativos", rows.filter(row => row.situacao !== "Inativo").length, "mint"], ["shield", "Com e-mail", rows.filter(row => row.email).length, "blue"], ["pin", "Com endereço", rows.filter(row => row.cep || row.cidade).length, "peach"]];
  if (kind === "animais") cards = [["paw", "Animais cadastrados", rows.length, "peach"], ["tag", "Com microchip", rows.filter(row => row.microchip).length, "mint"], ["layers", "Espécies", new Set(rows.map(row => row.especie).filter(Boolean)).size, "lavender"]];
  if (kind === "veterinarios") cards = [["stethoscope", "Profissionais ativos", rows.filter(row => row.situacao !== "Inativo").length, "mint"], ["shield", "Senha confirmada", rows.filter(row => row.senhaConfirmada === "Sim").length, "blue"], ["users", "Total da equipe", rows.length, "lavender"]];
  return cards.length ? `<section class="collection-summary ${kind}-summary">${cards.map(([iconName, label, count, color]) => `<article class="summary-tile"><span class="summary-icon ${color}">${icon(iconName)}</span><span><small>${label}</small><strong>${count}</strong></span></article>`).join("")}</section>` : "";
}
function renderCollectionIntro(kind, rows) {
  const configs = {
    tutores: { icon: "users", eyebrow: "CADASTRO DE RESPONSÁVEIS", title: "Diretório de tutores", description: "Contatos, documentos e endereços organizados.", metric: rows.length, metricLabel: "tutores", action: "Novo tutor" },
    animais: { icon: "paw", eyebrow: "FICHAS DOS PACIENTES", title: "Animais da clínica", description: "Identificação, tutor responsável e microchip RFID.", metric: rows.length, metricLabel: "animais", action: "Novo animal" },
    veterinarios: { icon: "stethoscope", eyebrow: "EQUIPE CLÍNICA", title: "Profissionais autorizados", description: "Registros no CRMV e situação de acesso.", metric: rows.length, metricLabel: "profissionais", action: "Novo veterinário" },
    consultas: { icon: "calendar", eyebrow: "ROTINA DE ATENDIMENTO", title: "Agenda da clínica", description: "Consultas organizadas por data e horário.", metric: rows.filter(row => row.situacao === "Agendada").length, metricLabel: "agendadas", action: "Nova consulta" },
    vacinas: { icon: "syringe", eyebrow: "CONTROLE VACINAL", title: "Carteira de vacinação", description: "Aplicações, classificação e reforços previstos.", metric: rows.filter(row => row.proximaDose && row.situacao !== "Cancelada").length, metricLabel: "reforços", action: "Nova vacina" },
    integracoes: { icon: "plug", eyebrow: "SERVIÇOS EXTERNOS", title: "Comunicações governamentais", description: "Envios obrigatórios de aplicações antirrábicas.", metric: rows.filter(row => row.situacao === "Pendente" || row.situacao === "Enviada").length, metricLabel: "pendentes", action: "Reprocessar", actionType: "retry-all" }
  };
  const config = configs[kind];
  const actionType = config.actionType || "new";
  const actionIcon = actionType === "retry-all" ? icon("refresh") : icon("plus");
  const actionClass = actionType === "retry-all" ? "button button-outline" : "button button-primary";
  return `<section class="collection-intro collection-intro-${kind}"><span class="collection-intro-icon">${icon(config.icon)}</span><div class="collection-intro-copy"><span class="eyebrow">${config.eyebrow}</span><h2>${config.title}</h2><p>${config.description}</p></div><div class="collection-intro-metric"><strong>${config.metric}</strong><span>${config.metricLabel}</span></div><button class="${actionClass}" data-action="${actionType}"><span>${actionIcon}</span>${config.action}</button></section>`;
}
function renderCollection(kind) {
  const config = definitions[kind], items = store[kind] || [];
  const rows = kind === "integracoes" ? store.integracoes || [] : items;
  const content = document.querySelector("#page-content");
  const searchLabel = kind === "consultas" ? "Buscar animal, motivo ou profissional" : kind === "integracoes" ? "Buscar operação, animal ou protocolo" : `Buscar ${config.plural}`;
  const controls = kind === "tutores" ? "" : `<div class="collection-controls collection-controls-${kind}"><label class="search-box">${icon("search")}<input id="search" type="search" placeholder="${searchLabel}..."></label><span class="record-count"><strong id="count">${rows.length}</strong> ${rows.length === 1 ? "registro" : "registros"}</span>${kind !== "integracoes" ? `<button class="filter-button" id="filter-button">${icon("filter")}<span>Filtros</span></button>` : ""}</div>`;
  content.innerHTML = `${renderCollectionIntro(kind, rows)}${controls}<div id="collection-list" class="collection-list collection-list-${kind}">${renderCollectionList(kind, rows)}</div><div class="data-disclaimer">${icon("info")}<span>Os registros são gravados no banco de dados da clínica por meio da API.</span></div>`;
  const search = document.querySelector("#search");
  if (search) search.addEventListener("input", event => {
    const term = event.target.value.toLocaleLowerCase("pt-BR");
    const filteredRows = rows.filter(row => (columns[kind] || []).some(([key]) => String(valor(row, key)).toLocaleLowerCase("pt-BR").includes(term)));
    document.querySelector("#count").textContent = filteredRows.length;
    document.querySelector("#collection-list").innerHTML = renderCollectionList(kind, filteredRows);
  });
  const tutorSearch = document.querySelector("#tutor-search");
  if (tutorSearch) tutorSearch.addEventListener("input", event => {
    const term = event.target.value.toLocaleLowerCase("pt-BR");
    const filteredRows = rows.filter(person => [person.nome, person.cpf, person.email, person.telefone].some(value => String(value || "").toLocaleLowerCase("pt-BR").includes(term)));
    if (!filteredRows.some(person => person.id === tutorSelecionadoId)) tutorSelecionadoId = filteredRows[0]?.id || null;
    document.querySelector("#tutor-results").innerHTML = renderTutorResults(filteredRows, tutorSelecionadoId);
    document.querySelector("#tutor-profile").innerHTML = renderTutorProfile(filteredRows.find(person => person.id === tutorSelecionadoId));
  });
}
function renderCollectionList(kind, rows) {
  if (!rows.length && kind !== "integracoes" && kind !== "tutores") return renderEmptyCollection(kind);
  if (kind === "tutores") return renderTutors(rows);
  if (kind === "animais") return renderAnimals(rows);
  if (kind === "veterinarios") return renderVeterinarians(rows);
  if (kind === "consultas") return renderConsultations(rows);
  if (kind === "vacinas") return renderVaccines(rows);
  return renderTable(kind, rows, columns[kind]);
}
function renderEmptyCollection(kind) {
  const content = {
    tutores: ["users", "Sua lista de responsáveis começa aqui", "Cadastre tutores para manter contatos e endereços da clínica organizados.", "Criar primeiro tutor", "tutor"],
    animais: ["paw", "Nenhum animal cadastrado", "Adicione um animal e associe o tutor responsável e o microchip RFID.", "Cadastrar primeiro animal", "animal"],
    veterinarios: ["stethoscope", "Monte sua equipe clínica", "Cadastre os profissionais e seus registros no CRMV.", "Adicionar veterinário", "veterinario"],
    consultas: ["calendar", "Agenda livre por enquanto", "As consultas registradas aparecerão aqui organizadas por data e horário.", "Agendar consulta", "consulta"],
    vacinas: ["syringe", "Carteira de vacinação vazia", "Registre aplicações para acompanhar lotes e próximas doses.", "Registrar vacina", "vacina"]
  }[kind];
  const illustration = `<div class="empty-section-mark empty-mark-${kind}">${icon(content[0])}</div>`;
  return `<div class="section-empty section-empty-${kind}">${illustration}<div class="section-empty-copy"><span class="eyebrow">${kind === "consultas" ? "AGENDA" : kind === "vacinas" ? "IMUNIZAÇÃO" : "PRIMEIROS PASSOS"}</span><h3>${content[1]}</h3><p>${content[2]}</p><button class="button button-primary" data-action="new">${icon("plus")}${content[3]}</button></div></div>`;
}
function renderIntegrations() {
  renderCollection("integracoes");
  const body = document.querySelector("#page-content");
  const overview = document.createElement("section");
  overview.className = "integration-overview";
  const items = store.integracoes || [];
  overview.innerHTML = `<article><span class="overview-icon mint">${icon("shield")}</span><div><small>Confirmadas</small><strong>${items.filter(x => x.situacao === "Confirmada").length}</strong></div></article><article><span class="overview-icon peach">${icon("clock")}</span><div><small>Pendentes</small><strong>${items.filter(x => x.situacao === "Pendente" || x.situacao === "Enviada").length}</strong></div></article><article><span class="overview-icon lavender">${icon("alert")}</span><div><small>Com falha</small><strong>${items.filter(x => x.situacao === "Falha").length}</strong></div></article>`;
  body.insertBefore(overview, body.firstChild);
  const flow = document.createElement("section");
  flow.className = "integration-flow";
  flow.innerHTML = `<div class="flow-step"><span>01</span><div><strong>Aplicação registrada</strong><small>Vacina antirrábica aplicada na clínica</small></div></div><i class="flow-arrow">${icon("arrow")}</i><div class="flow-step"><span>02</span><div><strong>Envio solicitado</strong><small>Dados encaminhados à API governamental</small></div></div><i class="flow-arrow">${icon("arrow")}</i><div class="flow-step"><span>03</span><div><strong>Resposta recebida</strong><small>Protocolo, confirmação ou falha</small></div></div>`;
  body.insertBefore(flow, overview.nextSibling);
  const info = document.createElement("div");
  info.className = "integration-info";
  info.innerHTML = `${icon("info")}<p><strong>Comunicação antirrábica</strong><br>O backend registra a vacinação e envia a comunicação obrigatória à API governamental. Os estados, protocolos e novas tentativas serão carregados pela API da clínica.</p>`;
  body.insertBefore(info, body.querySelector(".collection-controls"));
}
function render() {
  if (!usuario) return renderLogin();
  const page = location.hash.replace("#", "") || "dashboard";
  const validPage = definitions[page] ? page : "dashboard";
  if (page !== validPage) location.hash = validPage;
  renderShell(validPage);
  if (validPage === "dashboard") renderDashboard();
  else if (validPage === "integracoes") renderIntegrations();
  else renderCollection(validPage);
}
function abrirFormulario(kind, item = null) {
  const dialog = document.querySelector("#record-dialog");
  const title = item ? `Editar ${definitions[kind].singular}` : `Novo ${definitions[kind].singular}`;
  dialog.innerHTML = `<form id="record-form" method="dialog"><div class="dialog-header"><div><span class="eyebrow">CADASTRO</span><h2>${title}</h2></div><button type="button" class="icon-button" data-close aria-label="Fechar">${icon("close")}</button></div><div class="form-grid">${camposDe(kind, item).map(field => fieldHtml(field, item?.[field[0]])).join("")}</div><p class="login-error" data-form-error role="alert" style="margin:10px 0 0"></p><div class="dialog-footer"><button type="button" class="button button-outline" data-close>Cancelar</button><button class="button button-primary" type="submit">${item ? "Salvar alterações" : "Salvar cadastro"} ${icon("arrow")}</button></div></form>`;
  for (const field of camposDe(kind, item)) {
    if (field[1] === "Sexo" || field[1] === "Situação" || field[1] === "Classificação" || field[1] === "Senha confirmada") {
      const input = dialog.querySelector(`[name="${field[0]}"]`);
      if (item?.[field[0]]) input.value = item[field[0]];
      else if (field[0] === "situacao") input.value = situacaoPadrao[kind] || "Ativo";
    }
  }
  if (item) for (const [name, value] of Object.entries(item)) {
    const input = dialog.querySelector(`[name="${name}"]`);
    if (input) input.value = value;
  }
  dialog.querySelectorAll("[data-close]").forEach(button => button.addEventListener("click", () => dialog.close()));
  const cepInput = dialog.querySelector('[name="cep"]');
  if (cepInput) cepInput.addEventListener("blur", async () => {
    const note = dialog.querySelector("[data-cep-message]");
    if (cepInput.value.replace(/\D/g, "").length !== 8) return;
    note.textContent = "Consultando ViaCEP…";
    note.classList.add("loading");
    try {
      const address = await buscarCep(cepInput.value);
      for (const [field, value] of [["logradouro", address.logradouro], ["bairro", address.bairro], ["cidade", address.localidade], ["uf", address.uf], ["ibge", address.ibge]]) {
        const input = dialog.querySelector(`[name="${field}"]`);
        if (input && value) input.value = value;
      }
      note.textContent = "Endereço localizado. Você pode editar os campos manualmente.";
      note.classList.remove("loading"); note.classList.add("success-note");
    } catch (error) {
      note.textContent = error.message; note.classList.remove("loading"); note.classList.add("error-note");
    }
  });
  if (kind === "animais") {
    const especieInput = dialog.querySelector('[name="especie"]');
    const racaInput = dialog.querySelector('[name="raca"]');
    const atualizarRacas = preferida => {
      const atual = preferida ?? racaInput.value;
      const especie = (store.especies || []).find(row => row.nome === especieInput.value);
      const nomes = especie ? (store.racas || []).filter(raca => raca.especieId === especie.id).map(raca => raca.nome) : [];
      racaInput.innerHTML = selectOptions(["Sem raça definida", ...nomes]);
      if ([...racaInput.options].some(option => option.value === atual)) racaInput.value = atual;
    };
    especieInput.addEventListener("change", () => atualizarRacas());
    atualizarRacas(item?.raca);
  }
  if (kind === "vacinas") dialog.querySelectorAll('[name="data"]').forEach(input => { input.max = hojeLocal(); });
  dialog.querySelector("#record-form").addEventListener("submit", async event => {
    event.preventDefault();
    const form = event.currentTarget;
    const erroBox = dialog.querySelector("[data-form-error]");
    const botao = form.querySelector('button[type="submit"]');
    const record = Object.fromEntries(new FormData(form).entries());
    erroBox.textContent = ""; botao.disabled = true;
    try {
      const resultado = await Api.salvar(kind, record, item, store);
      await recarregar();
      if (kind === "tutores") tutorSelecionadoId = resultado.id;
      dialog.close(); render();
      if (kind === "veterinarios" && (resultado.codigoConfirmacao || resultado.emailEnviado)) return abrirConfirmacao(resultado);
      mostrarToast(`${definitions[kind].singular} salvo no banco de dados.`);
    } catch (erro) {
      if (erro.parcial) {
        try { await recarregar(); } catch { /* mantém a lista atual */ }
        dialog.close(); render(); mostrarToast(erro.message, 7000);
      } else {
        erroBox.textContent = erro.message; botao.disabled = false;
      }
    }
  });
  dialog.showModal();
}
function mostrarDetalhes(kind, id) {
  const item = (store[kind] || []).find(row => row.id === id);
  if (!item) return;
  const dialog = document.querySelector("#record-dialog");
  dialog.innerHTML = `<div class="dialog-header"><div><span class="eyebrow">DETALHES DO REGISTRO</span><h2>${escapeHtml(item.nome || item.operacao || definitions[kind].singular)}</h2></div><button type="button" class="icon-button" data-close aria-label="Fechar">${icon("close")}</button></div><div class="detail-grid">${Object.entries(item).filter(([key]) => key !== "id" && key !== "historicoMicrochips" && key !== "microchipAtual").map(([key, value]) => `<div><small>${escapeHtml(key.replace(/[A-Z]/g, letter => ` ${letter}`).replace(/^./, letter => letter.toUpperCase()))}</small><strong>${escapeHtml(valor(item, key))}</strong></div>`).join("")}</div>${kind === "animais" ? `<div class="history-box"><strong>Histórico do animal</strong><p>Consultas: ${(store.consultas || []).filter(row => row.animal === id).length} &nbsp;·&nbsp; Vacinas: ${(store.vacinas || []).filter(row => row.animal === id).length}</p>${item.microchipAtual ? `<p><strong>Microchip atual</strong></p><p>${escapeHtml(item.microchipAtual.microchip)} · Vinculado em ${escapeHtml(item.microchipAtual.data)} · Veterinário: ${escapeHtml(item.microchipAtual.veterinario)}</p>` : "<p>Animal sem microchip vinculado.</p>"}${(item.historicoMicrochips || []).length ? `<p><strong>Vínculos anteriores do microchip</strong></p>${item.historicoMicrochips.map(link => `<p>${escapeHtml(link.microchip)} · ${escapeHtml(link.data)} · Veterinário: ${escapeHtml(link.veterinario)}</p>`).join("")}` : "<p>Sem substituições de microchip registradas.</p>"}<a href="#consultas">Ver consultas ${icon("arrow")}</a> &nbsp; <a href="#vacinas">Ver vacinas ${icon("arrow")}</a></div>` : ""}<div class="dialog-footer"><button class="button button-primary" data-close>Fechar</button></div>`;
  dialog.querySelectorAll("[data-close]").forEach(button => button.addEventListener("click", () => dialog.close()));
  dialog.showModal();
}
function mostrarToast(message, ms = 3000) {
  const toast = document.querySelector("#toast"); toast.textContent = message; toast.classList.add("visible");
  clearTimeout(mostrarToast.timer);
  mostrarToast.timer = setTimeout(() => toast.classList.remove("visible"), ms);
}
async function recarregar() { store = await Api.carregarTudo(); }
Api.onSessaoExpirada = () => {
  if (!usuario) return;
  usuario = null; store = {};
  renderLogin("Sua sessão expirou. Entre novamente.");
};
async function iniciar() {
  document.querySelector("#app").innerHTML = `<main class="login-page"><p class="muted">Carregando…</p></main>`;
  try { usuario = await Api.me(); await recarregar(); } catch { usuario = null; }
  render();
}
function abrirConfirmacao(veterinario) {
  const dialog = document.querySelector("#record-dialog");
  dialog.innerHTML = `<form id="confirm-form" method="dialog"><div class="dialog-header"><div><span class="eyebrow">CONFIRMAÇÃO DE SENHA</span><h2>Confirme o acesso do veterinário</h2></div><button type="button" class="icon-button" data-close aria-label="Fechar">${icon("close")}</button></div><p class="muted">${veterinario.codigoConfirmacao ? "Simulação do envio por e-mail: o código abaixo seria enviado para" : "Enviamos um código de 6 dígitos para"} <strong>${escapeHtml(veterinario.email)}</strong>${veterinario.codigoConfirmacao ? "" : ". Digite-o abaixo"} e expira em 20 minutos.</p><div class="form-grid"><label class="field"><span>Código de confirmação</span><input name="codigo" value="${escapeHtml(veterinario.codigoConfirmacao || "")}" required inputmode="numeric" maxlength="6"></label></div><p class="login-error" data-form-error role="alert" style="margin:10px 0 0"></p><div class="dialog-footer"><button type="button" class="button button-outline" data-close>Confirmar depois</button><button class="button button-primary" type="submit">Confirmar senha ${icon("arrow")}</button></div></form>`;
  dialog.querySelectorAll("[data-close]").forEach(button => button.addEventListener("click", () => dialog.close()));
  dialog.querySelector("#confirm-form").addEventListener("submit", async event => {
    event.preventDefault();
    const erroBox = dialog.querySelector("[data-form-error]");
    const botao = event.currentTarget.querySelector('button[type="submit"]');
    erroBox.textContent = ""; botao.disabled = true;
    try {
      await Api.confirmarSenha(veterinario.email, new FormData(event.currentTarget).get("codigo"));
      await recarregar();
      dialog.close(); render(); mostrarToast("Senha confirmada. O veterinário já pode entrar no sistema.");
    } catch (erro) {
      erroBox.textContent = erro.message; botao.disabled = false;
    }
  });
  dialog.showModal();
}
function abrirSituacaoVacina(item) {
  if (item.situacao !== "Agendada") return mostrarToast("Somente vacinas agendadas podem mudar de situação.", 4500);
  const dialog = document.querySelector("#record-dialog");
  dialog.innerHTML = `<form id="vacina-form" method="dialog"><div class="dialog-header"><div><span class="eyebrow">VACINA AGENDADA</span><h2>Atualizar situação</h2></div><button type="button" class="icon-button" data-close aria-label="Fechar">${icon("close")}</button></div><div class="form-grid"><label class="field"><span>Nova situação *</span><select name="situacao" required><option value="Aplicada">Aplicada</option><option value="Cancelada">Cancelada</option></select></label><label class="field"><span>Data de aplicação</span><input name="data" type="date" value="${hojeLocal()}" max="${hojeLocal()}"></label></div><p class="login-error" data-form-error role="alert" style="margin:10px 0 0"></p><div class="dialog-footer"><button type="button" class="button button-outline" data-close>Cancelar</button><button class="button button-primary" type="submit">Salvar ${icon("arrow")}</button></div></form>`;
  dialog.querySelectorAll("[data-close]").forEach(button => button.addEventListener("click", () => dialog.close()));
  dialog.querySelector("#vacina-form").addEventListener("submit", async event => {
    event.preventDefault();
    const dados = Object.fromEntries(new FormData(event.currentTarget).entries());
    const erroBox = dialog.querySelector("[data-form-error]");
    const botao = event.currentTarget.querySelector('button[type="submit"]');
    erroBox.textContent = ""; botao.disabled = true;
    try {
      await Api.alterarSituacaoVacina(item.id, dados.situacao, dados.data);
      await recarregar();
      dialog.close(); render(); mostrarToast("Situação da vacina atualizada.");
    } catch (erro) {
      erroBox.textContent = erro.message; botao.disabled = false;
    }
  });
  dialog.showModal();
}
document.addEventListener("click", async event => {
  const button = event.target.closest("[data-action]");
  if (!button) return;
  const page = location.hash.slice(1) || "dashboard", action = button.dataset.action, id = button.dataset.id;
  if (action === "select-tutor") {
    tutorSelecionadoId = id;
    const term = document.querySelector("#tutor-search")?.value.toLocaleLowerCase("pt-BR") || "";
    const tutors = (store.tutores || []).filter(person => [person.nome, person.cpf, person.email, person.telefone].some(value => String(value || "").toLocaleLowerCase("pt-BR").includes(term)));
    document.querySelector("#tutor-results").innerHTML = renderTutorResults(tutors, tutorSelecionadoId);
    document.querySelector("#tutor-profile").innerHTML = renderTutorProfile((store.tutores || []).find(person => person.id === id));
    return;
  }
  if (action === "new") return abrirFormulario(page);
  if (action === "view") return mostrarDetalhes(page, id);
  if (action === "edit") {
    const item = (store[page] || []).find(row => row.id === id);
    if (page === "vacinas") return item && abrirSituacaoVacina(item);
    return abrirFormulario(page, item);
  }
  if (action === "deactivate") {
    const item = store[page]?.find(row => row.id === id);
    if (!item || !window.confirm(`Inativar "${item.nome || "este cadastro"}"?`)) return;
    try { await Api.inativar(page, id); await recarregar(); render(); mostrarToast("Cadastro inativado."); }
    catch (erro) { mostrarToast(erro.message, 6000); }
    return;
  }
  if (action === "retry") {
    button.disabled = true;
    try {
      const resultado = await Api.tentarNovamente(id);
      await recarregar(); render();
      mostrarToast(resultado.situacao === "CONFIRMADA"
        ? `Integração confirmada. Protocolo ${resultado.protocoloExterno}.`
        : `A API governamental não confirmou: ${resultado.mensagemErro || resultado.situacao}`, 6000);
    } catch (erro) { button.disabled = false; mostrarToast(erro.message, 6000); }
    return;
  }
  if (action === "retry-all") {
    button.disabled = true;
    try {
      const resumo = await Api.reprocessarPendentes(store.integracoes || []);
      await recarregar(); render();
      mostrarToast(resumo.total ? `${resumo.confirmadas} de ${resumo.total} integrações confirmadas.` : "Não há integrações pendentes ou com falha.", 5000);
    } catch (erro) { button.disabled = false; mostrarToast(erro.message, 6000); }
  }
});
document.addEventListener("click", event => {
  if (event.target.id === "filter-button") mostrarToast("Filtros de situação disponíveis em breve.");
});
window.addEventListener("hashchange", render);
iniciar();
