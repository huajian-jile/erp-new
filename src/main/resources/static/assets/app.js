const $ = (sel) => document.querySelector(sel);
const $$ = (sel) => Array.from(document.querySelectorAll(sel));

function showModal(modalId) {
  const trigger = document.getElementById(modalId + "-trigger");
  if (trigger) {
    trigger.click();
  } else {
    const el = document.getElementById(modalId);
    if (el) {
      el.classList.add("show");
      el.style.display = "block";
      document.body.classList.add("modal-open");
      const b = document.createElement("div");
      b.className = "modal-backdrop fade show";
      document.body.appendChild(b);
    }
  }
}

function hideModal(modalId) {
  const el = document.getElementById(modalId);
  if (!el) return;
  const closeBtn = el.querySelector('[data-bs-dismiss="modal"]');
  if (closeBtn) closeBtn.click();
  else {
    el.classList.remove("show");
    el.style.display = "none";
    document.body.classList.remove("modal-open");
    document.querySelectorAll(".modal-backdrop").forEach((b) => b.remove());
  }
}

async function apiGet(path) {
  const res = await fetch(path, { headers: { "Accept": "application/json" } });
  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  return res.json();
}

async function apiPostJson(path, body) {
  const res = await fetch(path, {
    method: "POST",
    headers: { "Content-Type": "application/json", "Accept": "application/json" },
    body: JSON.stringify(body ?? {})
  });
  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  const txt = await res.text();
  if (!txt) return null;
  return JSON.parse(txt);
}

async function apiPutJson(path, body) {
  const res = await fetch(path, {
    method: "PUT",
    headers: { "Content-Type": "application/json", "Accept": "application/json" },
    body: JSON.stringify(body ?? {})
  });
  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
  const txt = await res.text();
  if (!txt) return null;
  return JSON.parse(txt);
}

async function apiDelete(path) {
  const res = await fetch(path, { method: "DELETE" });
  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || `HTTP ${res.status}`);
  }
}

function moneyCentToYuan(priceCent) {
  if (priceCent == null) return "";
  return (priceCent / 100).toFixed(2);
}

function pct(x) {
  if (x == null) return "";
  const n = Number(x);
  if (!Number.isFinite(n)) return "";
  return (n * 100).toFixed(2) + "%";
}

function setActiveView(name) {
  const nav = $("#top-app-nav");
  if (nav) {
    nav.querySelectorAll("[data-view]").forEach(el => {
      el.classList.toggle("active", el.dataset.view === name);
    });
    nav.querySelectorAll(".nav-item.dropdown").forEach(li => {
      const hit = li.querySelector(`[data-view="${name}"]`);
      li.classList.toggle("active", !!hit);
      const toggle = li.querySelector(".dropdown-toggle");
      if (toggle) toggle.classList.toggle("fw-semibold", !!hit);
    });
  }
  $$(".view").forEach(v => v.classList.add("d-none"));
  const mapped = (name === "store-kpi") ? "ops" : name;
  const viewEl = $(`#view-${mapped}`);
  if (viewEl) viewEl.classList.remove("d-none");
}

let allowedViews = new Set();
let wmsCanWrite = false;

function isViewAllowed(viewKey) {
  return allowedViews.has(viewKey);
}

async function initPermissions() {
  const me = await apiGet("/api/wms/auth/me");
  allowedViews = new Set(me.allowedViews ?? []);
  wmsCanWrite = !!me.canWrite;

  const hint = $("#nav-readonly-hint");
  if (hint) hint.classList.toggle("d-none", wmsCanWrite);
  const npPanel = $("#np-create-panel");
  if (npPanel) npPanel.classList.toggle("d-none", !wmsCanWrite);

  const nav = $("#top-app-nav");
  if (!nav) return;

  nav.querySelectorAll("[data-view]").forEach(el => {
    const key = el.dataset.view;
    el.style.display = allowedViews.has(key) ? "" : "none";
  });

  // 下拉分组：组内全部无权限则隐藏整组
  nav.querySelectorAll(".nav-item.dropdown").forEach(li => {
    const items = li.querySelectorAll("[data-view]");
    const any = Array.from(items).some(i => i.style.display !== "none");
    li.style.display = any ? "" : "none";
  });

  // 单按钮项（销量/滞销/订单/统计）
  nav.querySelectorAll(".nav-item:not(.dropdown)").forEach(li => {
    const btn = li.querySelector("[data-view]");
    if (!btn) return;
    li.style.display = btn.style.display !== "none" ? "" : "none";
  });

  const cur = nav.querySelector("[data-view].active");
  const curOk = cur && cur.style.display !== "none" && allowedViews.has(cur.dataset.view);
  if (!curOk) {
    const first = Array.from(nav.querySelectorAll("[data-view]")).find(el => el.style.display !== "none");
    if (first) setActiveView(first.dataset.view);
  }
}

async function logDirectoryUsage(dirKey) {
  // 记录失败不影响前端使用
  try {
    await apiPostJson("/api/wms/auth/dir-usage", { directoryKey: dirKey });
  } catch (e) {
    // ignore
  }
}

async function loadProducts() {
  const products = await apiGet("/api/wms/catalog/products");
  $("#tbl-products").innerHTML = products.map(p =>
    `<tr><td>${p.id}</td><td>${p.code}</td><td>${p.name}</td><td>${p.status}</td></tr>`
  ).join("");
}

async function loadVariants() {
  const skus = await apiGet("/api/wms/catalog/skus");
  $("#tbl-skus").innerHTML = skus.map(s =>
    `<tr>
      <td>${s.id}</td>
      <td>${s.sku}</td>
      <td>${s.title}</td>
      <td>¥${moneyCentToYuan(s.priceCent)}</td>
      <td>${wmsCanWrite ? `<button class="btn btn-sm btn-outline-primary" data-price="${s.id}">调价</button>` : '<span class="text-secondary small">只读</span>'}</td>
    </tr>`
  ).join("");

  // 给 BI 的 sku 下拉复用
  const sel = $("#bi-sku");
  sel.innerHTML = skus.map(s => `<option value="${s.id}">${s.sku} - ${s.title}</option>`).join("");

  $("#tbl-skus").querySelectorAll("button[data-price]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-price");
      const v = prompt("输入新价格（分），例如 399：");
      if (v == null) return;
      const newPriceCent = Number(v);
      if (!Number.isFinite(newPriceCent) || newPriceCent < 0) return alert("价格不合法");
      await apiPostJson(`/api/wms/catalog/skus/${id}/price:change`, {
        newPriceCent,
        operator: "ui",
        reason: "UI调价"
      });
      await loadVariants();
      alert("调价成功");
    });
  });
}

async function loadPlatforms() {
  const platforms = await apiGet("/api/wms/platforms");
  $("#sel-platform").innerHTML = platforms.map(p => `<option value="${p.id}">${p.code} - ${p.name}</option>`).join("");
}

async function loadStores() {
  const platformId = $("#sel-platform").value;
  const stores = await apiGet(`/api/wms/stores?platformId=${encodeURIComponent(platformId)}`);
  $("#tbl-stores").innerHTML = stores.map(s =>
    `<tr>
      <td>${s.id}</td><td>${s.code}</td><td>${s.name}</td>
      <td>${wmsCanWrite ? `<button class="btn btn-sm btn-outline-secondary" data-issue="${s.id}">签发密钥</button>` : '<span class="text-secondary small">只读</span>'}</td>
    </tr>`
  ).join("");

  const selStore = $("#sel-store");
  const biStore = $("#bi-store");
  const opts = stores.map(s => `<option value="${s.id}">${s.code} - ${s.name}</option>`).join("");
  selStore.innerHTML = opts;
  biStore.innerHTML = opts;

  $("#tbl-stores").querySelectorAll("button[data-issue]").forEach(btn => {
    btn.addEventListener("click", async () => {
      $("#issued-key").classList.add("d-none");
      const id = btn.getAttribute("data-issue");
      const issued = await apiPostJson(`/api/wms/stores/${id}/api-keys:issue`, {});
      $("#issued-key").textContent = JSON.stringify(issued, null, 2);
      $("#issued-key").classList.remove("d-none");
    });
  });
}

async function loadInventory() {
  const storeId = $("#sel-store").value;
  const rows = await apiGet(`/api/wms/inventory/snapshot?storeId=${encodeURIComponent(storeId)}`);
  $("#tbl-inv").innerHTML = rows.map(r =>
    `<tr>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.qty}</td>
      <td>¥${moneyCentToYuan(r.priceCent)}</td>
      <td>¥${moneyCentToYuan(r.stockValueCent)}</td>
      <td>${r.updatedAt ?? ""}</td>
      <td class="d-flex gap-2">
        ${wmsCanWrite ? `
        <button class="btn btn-sm btn-outline-success" data-stock-add="${r.skuId}">+库存</button>
        <button class="btn btn-sm btn-outline-danger" data-stock-sub="${r.skuId}">-库存</button>
        <button class="btn btn-sm btn-outline-secondary" data-repl="${r.skuId}">备货</button>` : '<span class="text-secondary small">只读</span>'}
      </td>
    </tr>`
  ).join("");

  $("#tbl-inv").querySelectorAll("button[data-stock-add]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const skuId = Number(btn.getAttribute("data-stock-add"));
      const v = prompt("增加库存数量（整数）：");
      if (v == null) return;
      const delta = parseInt(v, 10);
      if (!Number.isFinite(delta) || delta <= 0) return alert("数量不合法");
      await apiPostJson("/api/wms/inventory/stock:adjust", {
        storeId: Number($("#sel-store").value),
        skuId,
        delta,
        reason: "UI_ADJUST",
        refNo: "UI"
      });
      await loadInventory();
      alert("库存已增加");
    });
  });

  $("#tbl-inv").querySelectorAll("button[data-stock-sub]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const skuId = Number(btn.getAttribute("data-stock-sub"));
      const v = prompt("减少库存数量（整数）：");
      if (v == null) return;
      const qty = parseInt(v, 10);
      if (!Number.isFinite(qty) || qty <= 0) return alert("数量不合法");
      await apiPostJson("/api/wms/inventory/stock:adjust", {
        storeId: Number($("#sel-store").value),
        skuId,
        delta: -qty,
        reason: "UI_ADJUST",
        refNo: "UI"
      });
      await loadInventory();
      alert("库存已减少");
    });
  });

  $("#tbl-inv").querySelectorAll("button[data-repl]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const skuId = Number(btn.getAttribute("data-repl"));
      const v = prompt("备货数量（整数）：");
      if (v == null) return;
      const planQty = parseInt(v, 10);
      if (!Number.isFinite(planQty) || planQty <= 0) return alert("数量不合法");
      const platformId = Number($("#sel-platform").value);
      const storeId = Number($("#sel-store").value);
      const plan = await apiPostJson("/api/wms/replenishment/plans", {
        platformId,
        storeId,
        skuId,
        planQty,
        createdBy: "ui"
      });
      await apiPostJson(`/api/wms/replenishment/${plan.id}:execute`, {});
      await loadInventory();
      alert(`备货已执行：单号 ${plan.id}`);
    });
  });
}

// ===== 库存变动（待分配/已分配/同步/快照）=====
function setInvTab(tab) {
  $$("#inv-change-tabs .nav-link").forEach(b => b.classList.toggle("active", b.dataset.tab === tab));
  $("#inv-tab-pending").classList.toggle("d-none", tab !== "pending");
  $("#inv-tab-allocated").classList.toggle("d-none", tab !== "allocated");
  $("#inv-tab-sync").classList.toggle("d-none", tab !== "sync");
  $("#inv-tab-clear").classList.toggle("d-none", tab !== "clear");
  $("#inv-tab-snapshot").classList.toggle("d-none", tab !== "snapshot");
  $("#inv-tab-purchase").classList.toggle("d-none", tab !== "purchase");
  $("#inv-tab-transfer").classList.toggle("d-none", tab !== "transfer");
}

async function loadInvPending() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#sel-store").value;
  if (!platformId || !storeId) return;
  const rows = await apiGet(`/api/wms/inventory-change/pending?platformId=${platformId}&storeId=${storeId}&limit=200`);
  $("#tbl-inv-pending").innerHTML = rows.map(r => `
    <tr>
      <td>${r.storeCode}</td>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.qty7d}</td>
      <td>${r.avgDailyQty7d}</td>
      <td>${r.storeStockQty}</td>
      <td class="fw-semibold text-danger">${r.needAllocQty}</td>
      <td>${wmsCanWrite ? `<button class="btn btn-sm btn-outline-primary" data-alloc="${r.skuId}" data-need="${r.needAllocQty}">分配</button>` : '<span class="text-secondary small">只读</span>'}</td>
    </tr>
  `).join("");

  $("#tbl-inv-pending").querySelectorAll("button[data-alloc]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const skuId = Number(btn.getAttribute("data-alloc"));
      const need = Number(btn.getAttribute("data-need"));
      const v = prompt(`分配数量（建议=${need}）：`, String(need));
      if (v == null) return;
      const allocQty = parseInt(v, 10);
      if (!Number.isFinite(allocQty) || allocQty <= 0) return alert("数量不合法");
      await apiPostJson("/api/wms/inventory-change/allocations", {
        platformId: Number($("#sel-platform").value),
        storeId: Number($("#sel-store").value),
        skuId,
        allocQty,
        operator: "ui"
      });
      setInvTab("allocated");
      await loadInvAllocated();
    });
  });
}

async function loadInvAllocated() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#sel-store").value;
  if (!platformId || !storeId) return;
  const rows = await apiGet(`/api/wms/inventory-change/allocations?platformId=${platformId}&storeId=${storeId}&limit=200`);
  $("#tbl-inv-alloc").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.storeCode}</td>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.allocQty}</td>
      <td><span class="badge text-bg-secondary">${r.status}</span></td>
      <td>${r.createdAt ?? ""}</td>
      <td class="d-flex gap-2 flex-wrap">
        ${wmsCanWrite ? `
        <button class="btn btn-sm btn-outline-success" data-sync="${r.id}">同步</button>
        <button class="btn btn-sm btn-outline-danger" data-clear="${r.id}">清空其它店铺</button>` : '<span class="text-secondary small">只读</span>'}
      </td>
    </tr>
  `).join("");

  $("#tbl-inv-alloc").querySelectorAll("button[data-sync]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-sync");
    await apiPostJson(`/api/wms/inventory-change/allocations/${id}:sync`, {});
    await loadInvSyncLogs();
    await loadInvAllocated();
  }));
  $("#tbl-inv-alloc").querySelectorAll("button[data-clear]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-clear");
    if (!confirm("确认清空同平台其它店铺的该SKU库存？")) return;
    await apiPostJson(`/api/wms/inventory-change/allocations/${id}:clear-other-stores`, {});
    await loadInventory();
    await loadInvAllocated();
  }));
}

async function loadInvSyncLogs() {
  const storeId = $("#sel-store").value;
  if (!storeId) return;
  const rows = await apiGet(`/api/wms/inventory-change/sync-logs?storeId=${encodeURIComponent(storeId)}&limit=500`);
  $("#tbl-inv-sync").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.allocationId}</td>
      <td>${r.storeId}</td>
      <td>${r.skuId}</td>
      <td>${r.allocQty}</td>
      <td>${r.success ? "Y" : "N"}</td>
      <td>${r.createdAt ?? ""}</td>
    </tr>
  `).join("");
}

async function loadInvClearRecords() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#sel-store").value;
  if (!platformId || !storeId) return;
  const rows = await apiGet(`/api/wms/inventory-change/clear-records?platformId=${platformId}&storeId=${storeId}&limit=500`);
  $("#tbl-inv-clear").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.sourceStoreCode}</td>
      <td>${r.clearedStoreCode}</td>
      <td>${r.sku}</td>
      <td>${r.clearedQty}</td>
      <td>${r.createdAt ?? ""}</td>
    </tr>
  `).join("");
}

let chart;
async function loadBi() {
  const storeId = $("#bi-store").value;
  const skuId = $("#bi-sku").value;
  const from = $("#bi-from").value;
  const to = $("#bi-to").value;
  if (!storeId || !skuId || !from || !to) return;
  const data = await apiGet(`/api/wms/bi/stock-delta/daily?storeId=${storeId}&skuId=${skuId}&from=${from}&to=${to}`);
  const labels = data.map(x => x.day);
  const values = data.map(x => x.deltaSum);

  const ctx = $("#bi-chart");
  if (chart) chart.destroy();
  chart = new Chart(ctx, {
    type: "line",
    data: {
      labels,
      datasets: [{
        label: "每日库存变动 (delta)",
        data: values,
        borderWidth: 2,
        tension: 0.25
      }]
    },
    options: {
      responsive: true,
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
}

async function loadSqlSnippets() {
  // 直接把服务端静态文件当文本读（避免再写后端接口）
  const paths = {
    "#sql-schema": "/sql/01_wms_schema.sql",
    // 平台/备货
    // "#sql-platform": "/sql/04_wms_platform_and_replenishment.sql",
    // 多仓库/单据
    // "#sql-warehouse": "/sql/05_wms_warehouse_and_orders.sql",
    "#sql-views": "/sql/02_wms_views.sql",
    // "#sql-analytics": "/sql/06_wms_analytics_views.sql",
    "#sql-fn": "/sql/03_wms_functions_and_materialized.sql",
    "#sql-mock": "/sql/10_wms_mock_data.sql",
  };
  await Promise.all(Object.entries(paths).map(async ([sel, path]) => {
    const res = await fetch(path);
    $(sel).textContent = await res.text();
  }));
}

let adminSelectedDirKey = null;

async function loadUsageTopDirectories() {
  const rows = await apiGet(`/api/wms/admin/usage/top-directories?limit=10`);
  const tb = $("#tbl-usage-top-dirs");
  tb.innerHTML = rows.map(r => `
    <tr>
      <td><a href="#" data-dirkey="${r.dirKey}">${r.dirName} <span class="text-secondary small">(${r.dirKey})</span></a></td>
      <td>${r.usageCount}</td>
    </tr>
  `).join("");

  const first = rows[0]?.dirKey ?? null;
  if (!adminSelectedDirKey && first) adminSelectedDirKey = first;

  tb.querySelectorAll("a[data-dirkey]").forEach(a => {
    a.addEventListener("click", async (e) => {
      e.preventDefault();
      adminSelectedDirKey = a.getAttribute("data-dirkey");
      await loadUsageTopUsers(adminSelectedDirKey);
    });
  });

  if (adminSelectedDirKey) await loadUsageTopUsers(adminSelectedDirKey);
}

let accountAdminRoles = [];

async function loadAccountAdmin() {
  accountAdminRoles = await apiGet("/api/wms/admin/roles");
  await loadAccountsTable();
  await loadRolesPermissionsTable();
}

async function loadAccountsTable() {
  const rows = await apiGet("/api/wms/admin/accounts");
  const tb = $("#tbl-accounts");
  if (!tb) return;
  tb.innerHTML = rows.map(r => `
    <tr>
      <td>${escapeHtml(r.username)}</td>
      <td>${escapeHtml(r.displayName)}</td>
      <td><span class="badge ${r.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}">${escapeHtml(r.status)}</span></td>
      <td><code>${escapeHtml(r.passwordDisplay)}</code></td>
      <td>${(r.roles || []).map(x => escapeHtml(x)).join(", ")}</td>
      <td><small>${(r.allowedViews || []).slice(0, 10).map(x => escapeHtml(x)).join(", ")}${(r.allowedViews || []).length > 10 ? "…" : ""}</small></td>
      <td>
        <button class="btn btn-sm btn-outline-primary me-1" data-edit-account="${r.id}">编辑</button>
        <button class="btn btn-sm btn-outline-warning me-1" data-reset-pwd="${r.id}">重置密码</button>
        <button class="btn btn-sm btn-outline-danger" data-del-account="${r.id}" data-username="${escapeHtml(r.username)}">删除</button>
      </td>
    </tr>
  `).join("");

  tb.querySelectorAll("button[data-edit-account]").forEach(btn => {
    btn.addEventListener("click", () => openAccountModal(Number(btn.getAttribute("data-edit-account"))).catch(e => alert(e.message)));
  });
  tb.querySelectorAll("button[data-reset-pwd]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const userId = Number(btn.getAttribute("data-reset-pwd"));
      const row = rows.find(r => r.id === userId);
      const v = prompt(`为账号 ${row?.username || userId} 输入新密码：`);
      if (v == null) return;
      const pwd = String(v).trim();
      if (!pwd) return alert("密码不能为空");
      try {
        await apiPutJson(`/api/wms/admin/accounts/${userId}/password`, { newPassword: pwd });
        alert("密码已重置");
        await loadAccountsTable();
      } catch (e) { alert(e.message || String(e)); }
    });
  });
  tb.querySelectorAll("button[data-del-account]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const userId = Number(btn.getAttribute("data-del-account"));
      const username = btn.getAttribute("data-username");
      if (!confirm(`确定删除账号 ${username}？`)) return;
      try {
        await apiDelete(`/api/wms/admin/accounts/${userId}`);
        await loadAccountsTable();
      } catch (e) { alert(e.message || String(e)); }
    });
  });
}

let accountAdminDirectories = [];

async function loadRolesPermissionsTable() {
  const rows = await apiGet("/api/wms/admin/roles-with-directories");
  const tb = $("#tbl-roles-permissions");
  if (!tb) return;
  tb.innerHTML = rows.map(r => `
    <tr>
      <td class="fw-semibold">${escapeHtml(r.roleName)} <span class="text-secondary small">(${escapeHtml(r.roleCode)})</span></td>
      <td><small>${(r.dirNames || []).length ? (r.dirNames || []).map(x => escapeHtml(x)).join("、") : "无"}</small></td>
      <td>
        <button class="btn btn-sm btn-outline-primary me-1" data-edit-role-perms="${r.roleId}">编辑</button>
        <button class="btn btn-sm btn-outline-danger" data-del-role="${r.roleId}" data-role-name="${escapeHtml(r.roleName)}">删除</button>
      </td>
    </tr>
  `).join("");

  tb.querySelectorAll("button[data-edit-role-perms]").forEach(btn => {
    btn.addEventListener("click", () => openRolePermissionsModal(Number(btn.getAttribute("data-edit-role-perms"))).catch(e => alert(e.message)));
  });
  tb.querySelectorAll("button[data-del-role]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const roleId = Number(btn.getAttribute("data-del-role"));
      const roleName = btn.getAttribute("data-role-name");
      if (!confirm(`确定删除权限「${roleName}」？删除后，拥有该角色的账号将失去该角色。`)) return;
      try {
        await apiDelete(`/api/wms/admin/roles/${roleId}`);
        accountAdminRoles = await apiGet("/api/wms/admin/roles");
        await loadRolesPermissionsTable();
      } catch (e) { alert(e.message || String(e)); }
    });
  });
}

function bindAccountAdminTabs() {
  $$("#account-admin-tabs .nav-link").forEach(btn => {
    btn.addEventListener("click", () => {
      const tab = btn.dataset.tab;
      $$("#account-admin-tabs .nav-link").forEach(b => b.classList.toggle("active", b.dataset.tab === tab));
      $("#panel-accounts").classList.toggle("d-none", tab !== "accounts");
      $("#panel-permissions").classList.toggle("d-none", tab !== "permissions");
    });
  });
}

function renderRoleCheckboxes(containerId, selectedRoleIds) {
  const sel = new Set((selectedRoleIds || []).map(Number));
  const html = accountAdminRoles.map(r => `
    <div class="form-check form-check-inline"><input class="form-check-input" type="checkbox" id="role-${r.id}" value="${r.id}" ${sel.has(r.id) ? "checked" : ""}/>
    <label class="form-check-label small" for="role-${r.id}">${escapeHtml(r.name)}(${escapeHtml(r.code)})</label></div>
  `).join("");
  const el = document.getElementById(containerId);
  if (el) el.innerHTML = html;
}

function getSelectedRoleIds(containerId) {
  const el = document.getElementById(containerId);
  if (!el) return [];
  return Array.from(el.querySelectorAll("input:checked")).map(cb => Number(cb.value));
}

async function openAccountModal(userId) {
  if (!accountAdminRoles.length) accountAdminRoles = await apiGet("/api/wms/admin/roles");
  $("#modal-account-title").textContent = userId ? "编辑账号" : "新增账号";
  $("#account-id").value = userId || "";
  $("#account-username").value = "";
  $("#account-username").disabled = !!userId;
  $("#account-password").value = "";
  $("#account-password").required = !userId;
  $("#account-displayName").value = "";
  $("#account-status").value = "ACTIVE";
  if (userId) {
    const r = await apiGet(`/api/wms/admin/accounts/${userId}`);
    $("#account-username").value = r.username;
    $("#account-displayName").value = r.displayName || "";
    $("#account-status").value = r.status || "ACTIVE";
    renderRoleCheckboxes("account-roles-checkboxes", r.roleIds);
  } else {
    renderRoleCheckboxes("account-roles-checkboxes", []);
  }
  showModal("modal-account");
}

async function openRolePermissionsModal(roleId) {
  if (!accountAdminDirectories.length) accountAdminDirectories = await apiGet("/api/wms/admin/directories");
  const isCreate = !roleId;
  $("#modal-role-permissions-title").textContent = isCreate ? "新增权限" : "编辑权限";
  $("#role-permissions-roleId").value = roleId || "";
  const codeRow = document.getElementById("role-code-row");
  const codeInput = document.getElementById("role-permissions-code");
  if (codeRow) codeRow.style.display = isCreate ? "" : "none";
  if (codeInput) {
    codeInput.value = "";
    codeInput.disabled = !isCreate;
  }
  document.getElementById("role-permissions-name").value = "";

  let sel = new Set();
  if (!isCreate) {
    const rows = await apiGet("/api/wms/admin/roles-with-directories");
    const r = rows.find(x => x.roleId === roleId);
    if (!r) return;
    document.getElementById("role-permissions-name").value = r.roleName || "";
    if (codeInput) codeInput.value = r.roleCode || "";
    sel = new Set((r.permissionIds || []).map(Number));
  }
  const html = accountAdminDirectories.map(d => `
    <div class="form-check"><input class="form-check-input" type="checkbox" id="perm-${d.permissionId}" value="${d.permissionId}" ${sel.has(d.permissionId) ? "checked" : ""}/>
    <label class="form-check-label small" for="perm-${d.permissionId}">${escapeHtml(d.dirName)}</label></div>
  `).join("");
  document.getElementById("role-permissions-checkboxes").innerHTML = html;
  showModal("modal-role-permissions");
}

function getSelectedPermissionIds() {
  const el = document.getElementById("role-permissions-checkboxes");
  if (!el) return [];
  return Array.from(el.querySelectorAll("input:checked")).map(cb => Number(cb.value));
}

function bindAccountAdminModals() {
  $("#btn-add-account")?.addEventListener("click", () => openAccountModal(null).catch(e => alert(e.message)));
  $("#btn-save-account")?.addEventListener("click", async () => {
    const id = $("#account-id").value;
    const username = $("#account-username").value.trim();
    const password = $("#account-password").value;
    const displayName = $("#account-displayName").value.trim() || username;
    const status = $("#account-status").value;
    const roleIds = getSelectedRoleIds("account-roles-checkboxes");
    if (!username) return alert("账号不能为空");
    if (!id && !password) return alert("密码不能为空");
    try {
      if (id) {
        await apiPutJson(`/api/wms/admin/accounts/${id}`, { displayName, status, roleIds });
      } else {
        await apiPostJson("/api/wms/admin/accounts", { username, password, displayName, status, roleIds });
      }
      hideModal("modal-account");
      await loadAccountsTable();
    } catch (e) { alert(e.message || String(e)); }
  });
  $("#btn-add-role")?.addEventListener("click", () => openRolePermissionsModal(null).catch(e => alert(e.message)));
  $("#btn-save-role-permissions")?.addEventListener("click", async () => {
    const roleId = $("#role-permissions-roleId").value;
    const code = $("#role-permissions-code")?.value?.trim().toLowerCase().replace(/\s+/g, "-") || "";
    const name = $("#role-permissions-name")?.value?.trim() || "";
    const permissionIds = getSelectedPermissionIds();
    if (!roleId && !code) return alert("角色 Code 不能为空");
    try {
      if (roleId) {
        await apiPutJson(`/api/wms/admin/roles/${roleId}`, { name: name || undefined, permissionIds });
      } else {
        await apiPostJson("/api/wms/admin/roles", { code, name: name || code, permissionIds });
      }
      hideModal("modal-role-permissions");
      accountAdminRoles = await apiGet("/api/wms/admin/roles");
      await loadRolesPermissionsTable();
    } catch (e) { alert(e.message || String(e)); }
  });
}

let crudEntities = [];
let currentCrudEntity = null;

async function loadCrudAdmin() {
  crudEntities = await apiGet("/crud-entities.json");
  if (!Array.isArray(crudEntities) || crudEntities.length === 0) {
    crudEntities = [];
    $("#crud-entity-select").innerHTML = '<option value="">无 AutoCrud 实体</option>';
    $("#crud-table-head").innerHTML = "";
    $("#crud-table-body").innerHTML = "<tr><td colspan='10' class='text-secondary'>请先创建 @AutoCrud 实体并执行 mvn compile</td></tr>";
    return;
  }
  const sel = $("#crud-entity-select");
  sel.innerHTML = crudEntities.map(e => `<option value="${escapeHtml(e.apiPath)}">${escapeHtml(e.displayName)} (${escapeHtml(e.apiPath)})</option>`).join("");
  currentCrudEntity = crudEntities[0] || null;
  if (currentCrudEntity) sel.value = currentCrudEntity.apiPath;
  if (currentCrudEntity) await loadCrudTable(currentCrudEntity);
}

function bindCrudAdmin() {
  $("#crud-entity-select")?.addEventListener("change", () => {
    currentCrudEntity = crudEntities.find(x => x.apiPath === $("#crud-entity-select").value) || null;
    if (currentCrudEntity) loadCrudTable(currentCrudEntity).catch(err => alert(err.message));
  });
  $("#crud-btn-add")?.addEventListener("click", () => {
    if (!currentCrudEntity) return;
    openCrudModal(currentCrudEntity, null);
  });
  $("#crud-btn-refresh")?.addEventListener("click", async () => {
    if (currentCrudEntity) await loadCrudTable(currentCrudEntity).catch(e => alert(e.message));
  });
  $("#crud-btn-save")?.addEventListener("click", async () => saveCrud());
}

async function loadCrudTable(entity) {
  const rows = await apiGet(`/api/crud/${entity.apiPath}`);
  const arr = Array.isArray(rows) ? rows : (rows && typeof rows[Symbol.iterator] === "function" ? [...rows] : []);
  const thead = $("#crud-table-head");
  const tbody = $("#crud-table-body");
  thead.innerHTML = entity.fields.map(f => `<th>${escapeHtml(f.label)}</th>`).join("") + "<th>操作</th>";
  tbody.innerHTML = arr.map(row => {
    const cells = entity.fields.map(f => {
      let v = row[f.name];
      if (v == null) v = "";
      if (f.type === "datetime" && v) v = String(v).replace("T", " ");
      return `<td>${escapeHtml(String(v))}</td>`;
    }).join("");
    const id = row.id;
    return `<tr data-id="${id}">${cells}<td>
      <button class="btn btn-sm btn-outline-primary me-1" data-crud-edit="${id}">编辑</button>
      ${wmsCanWrite ? `<button class="btn btn-sm btn-outline-danger" data-crud-del="${id}">删除</button>` : ""}
    </td></tr>`;
  }).join("");
  tbody.querySelectorAll("[data-crud-edit]").forEach(btn => {
    btn.addEventListener("click", () => {
      const row = arr.find(r => r.id === Number(btn.getAttribute("data-crud-edit")));
      if (row) openCrudModal(entity, row);
    });
  });
  tbody.querySelectorAll("[data-crud-del]").forEach(btn => {
    btn.addEventListener("click", async () => {
      const id = btn.getAttribute("data-crud-del");
      if (!confirm("确定删除？")) return;
      try {
        await apiDelete(`/api/crud/${entity.apiPath}/${id}`);
        await loadCrudTable(entity);
      } catch (e) { alert(e.message || String(e)); }
    });
  });
}

function openCrudModal(entity, row) {
  const isEdit = !!row;
  $("#modal-crud-title").textContent = isEdit ? "编辑" : "新增";
  const body = $("#modal-crud-body");
  body.innerHTML = entity.fields.filter(f => f.editable).map(f => {
    const val = row ? (row[f.name] ?? "") : "";
    let input = "";
    if (f.type === "number") input = `<input type="number" class="form-control form-control-sm" data-crud-field="${f.name}" value="${escapeHtml(String(val))}"/>`;
    else if (f.type === "datetime") {
      const dv = val ? String(val).replace(" ", "T").slice(0, 16) : "";
      input = `<input type="datetime-local" class="form-control form-control-sm" data-crud-field="${f.name}" value="${dv}"/>`;
    }
    else if (f.type === "boolean") input = `<div class="form-check"><input type="checkbox" class="form-check-input" data-crud-field="${f.name}" ${val ? "checked" : ""}/><label class="form-check-label small">是</label></div>`;
    else input = `<input type="text" class="form-control form-control-sm" data-crud-field="${f.name}" value="${escapeHtml(String(val))}"/>`;
    return `<div class="mb-2"><label class="form-label small">${escapeHtml(f.label)}</label>${input}</div>`;
  }).join("");
  if (row) body.innerHTML += `<input type="hidden" data-crud-field="id" value="${row.id}"/>`;
  body.dataset.crudApiPath = entity.apiPath;
  body.dataset.crudIsEdit = isEdit ? "1" : "0";
  showModal("modal-crud");
}

async function saveCrud() {
  const body = $("#modal-crud-body");
  const apiPath = body.dataset.crudApiPath;
  const isEdit = body.dataset.crudIsEdit === "1";
  const payload = {};
  body.querySelectorAll("[data-crud-field]").forEach(el => {
    const name = el.getAttribute("data-crud-field");
    if (el.type === "checkbox") payload[name] = el.checked;
    else if (el.type === "number") payload[name] = el.value === "" ? null : Number(el.value);
    else payload[name] = el.value === "" ? null : el.value;
  });
  try {
    if (isEdit) {
      await apiPutJson(`/api/crud/${apiPath}/${payload.id}`, payload);
    } else {
      delete payload.id;
      await apiPostJson(`/api/crud/${apiPath}`, payload);
    }
    hideModal("modal-crud");
    if (currentCrudEntity && currentCrudEntity.apiPath === apiPath) await loadCrudTable(currentCrudEntity);
  } catch (e) { alert(e.message || String(e)); }
}

function escapeHtml(s) {
  if (s == null) return "";
  const div = document.createElement("div");
  div.textContent = s;
  return div.innerHTML;
}

async function loadUsageTopUsers(directoryKey) {
  if (!directoryKey) return;
  const rows = await apiGet(`/api/wms/admin/usage/top-users?directoryKey=${encodeURIComponent(directoryKey)}&limit=20`);
  $("#tbl-usage-top-users").innerHTML = rows.map(r => `
    <tr>
      <td>${r.displayName} <span class="text-secondary small">(${r.username})</span></td>
      <td>${r.usageCount}</td>
    </tr>
  `).join("");
}

async function loadWarehouseList() {
  const warehouses = await apiGet("/api/wms/warehouses");
  $("#tbl-warehouse-list").innerHTML = warehouses.map(w => `
    <tr>
      <td>${w.id}</td>
      <td>${w.code}</td>
      <td>${w.name}</td>
      <td>${w.status}</td>
      <td>${w.updatedAt ?? ""}</td>
    </tr>
  `).join("");
}

async function loadWarehousesSelect() {
  const warehouses = await apiGet("/api/wms/warehouses");
  $("#sel-warehouse").innerHTML = warehouses.map(w => `<option value="${w.id}">${w.code} - ${w.name}</option>`).join("");
}

async function loadWarehouseInventory() {
  const wid = $("#sel-warehouse").value;
  const rows = await apiGet(`/api/wms/warehouses/inventory/snapshot?warehouseId=${encodeURIComponent(wid)}`);
  $("#tbl-wh-inv").innerHTML = rows.map(r => `
    <tr>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.availableQty}</td>
      <td>${r.inTransitQty}</td>
      <td>${r.totalQty}</td>
      <td>¥${moneyCentToYuan(r.costCent)}</td>
      <td>¥${moneyCentToYuan(r.salePriceCent)}</td>
      <td>¥${moneyCentToYuan(r.profitPerUnitCent)}</td>
      <td>${pct(r.profitRate)}</td>
      <td>${r.dataRefreshedAt ?? ""}</td>
    </tr>
  `).join("");
}

async function loadRankSales() {
  const sales = await apiGet("/api/wms/analytics/rank/sales-qty-30d?limit=20");
  $("#tbl-rank-sales").innerHTML = sales.map((r, idx) =>
    `<tr><td>${idx + 1}</td><td>${r.sku}</td><td>${r.title}</td><td>${r.qty30d}</td></tr>`
  ).join("");
}

async function loadRankProfit() {
  const profit = await apiGet("/api/wms/analytics/rank/profit-30d?limit=20");
  $("#tbl-rank-profit").innerHTML = profit.map((r, idx) =>
    `<tr><td>${idx + 1}</td><td>${r.sku}</td><td>${r.title}</td><td>¥${moneyCentToYuan(r.profitCent30d)}</td><td>${pct(r.profitRate30d)}</td></tr>`
  ).join("");
}

function initDates() {
  const today = new Date();
  const to = today.toISOString().slice(0, 10);
  const fromDate = new Date(today);
  fromDate.setDate(today.getDate() - 7);
  const from = fromDate.toISOString().slice(0, 10);
  $("#bi-from").value = from;
  $("#bi-to").value = to;
  $("#ops-from").value = from;
  $("#ops-to").value = to;
  if ($("#orders-from")) $("#orders-from").value = from;
  if ($("#orders-to")) $("#orders-to").value = to;
}

let opsGmvChart;
let opsNetChart;
let opsAdsChart;
let opsFunnelChart;
async function loadOpsStoresFromCurrentPlatform() {
  const platformId = $("#sel-platform").value;
  const stores = await apiGet(`/api/wms/stores?platformId=${encodeURIComponent(platformId)}`);
  $("#ops-store").innerHTML = stores.map(s => `<option value="${s.id}">${s.code} - ${s.name}</option>`).join("");
}

async function loadStoreSelect(selId) {
  const platformId = $("#sel-platform").value;
  const stores = await apiGet(`/api/wms/stores?platformId=${encodeURIComponent(platformId)}`);
  $(selId).innerHTML = stores.map(s => `<option value="${s.id}">${s.code} - ${s.name}</option>`).join("");
}

async function loadOrders() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#orders-store").value;
  const from = $("#orders-from").value;
  const to = $("#orders-to").value;
  const status = $("#orders-status").value;
  if (!platformId || !storeId || !from || !to) return;
  const rows = await apiGet(`/api/wms/sales/orders?platformId=${platformId}&storeId=${storeId}&from=${from}&to=${to}&status=${encodeURIComponent(status)}&limit=200`);
  $("#tbl-orders").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td><a href="#" data-order-detail="${r.id}">${r.orderNo}</a></td>
      <td>${r.storeCode}</td>
      <td>${r.warehouseCode}</td>
      <td><span class="badge text-bg-secondary">${r.status}</span></td>
      <td>${r.itemQty}</td>
      <td>¥${moneyCentToYuan(r.amountCent)}</td>
      <td>${r.createdAt ?? ""}</td>
      <td>${r.paidAt ?? ""}</td>
      <td>${r.shippedAt ?? ""}</td>
      <td>${r.deliveredAt ?? ""}</td>
    </tr>
  `).join("");

  $("#orders-detail-box").classList.add("d-none");
  $("#tbl-orders").querySelectorAll("a[data-order-detail]").forEach(a => {
    a.addEventListener("click", async (e) => {
      e.preventDefault();
      const id = a.getAttribute("data-order-detail");
      const detail = await apiGet(`/api/wms/sales/orders/${id}`);
      $("#orders-detail").textContent = JSON.stringify(detail, null, 2);
      $("#orders-detail-box").classList.remove("d-none");
    });
  });
}

async function loadProfitTable() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#profit-store").value;
  if (!platformId || !storeId) return;
  const rows = await apiGet(`/api/wms/pricing/profit-table?platformId=${platformId}&storeId=${storeId}&limit=500`);
  $("#tbl-profit-table").innerHTML = rows.map(r => `
    <tr>
      <td>${r.productName} <span class="text-secondary small">(${r.productCode})</span></td>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>¥${moneyCentToYuan(r.priceCent)}</td>
      <td>¥${moneyCentToYuan(r.costCent)}</td>
      <td>¥${moneyCentToYuan(r.profitCent)}</td>
      <td>${r.profitMarginPct}</td>
      <td>${r.storeStockQty}</td>
      <td>¥${moneyCentToYuan(r.stockProfitCent)}</td>
      <td>${r.stockAgeDays ?? ""}</td>
      <td>${r.stockUpdatedAt ?? ""}</td>
    </tr>
  `).join("");
}

async function calcProfit() {
  const priceCent = $("#calc-price-cent").value;
  const costCent = $("#calc-cost-cent").value;
  if (priceCent === "" || costCent === "") return;
  const res = await apiGet(`/api/wms/pricing/profit/calc?priceCent=${encodeURIComponent(priceCent)}&costCent=${encodeURIComponent(costCent)}`);
  $("#calc-profit-cent").textContent = res.profitCent;
  $("#calc-profit-pct").textContent = res.profitMarginPct;
}

async function loadPriceTodos() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#todo-store").value;
  const minDays = $("#todo-min-days").value;
  if (!platformId || !storeId) return;
  const rows = await apiGet(`/api/wms/pricing/price-change-todos?platformId=${platformId}&storeId=${storeId}&minDays=${encodeURIComponent(minDays)}&limit=500`);
  $("#tbl-price-todos").innerHTML = rows.map(r => `
    <tr>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.storeStockQty}</td>
      <td>${r.stockAgeDays}</td>
      <td>${r.qty7d}</td>
      <td>¥${moneyCentToYuan(r.priceCent)}</td>
      <td>¥${moneyCentToYuan(r.costCent)}</td>
      <td>${r.profitMarginPct}</td>
      <td>${r.suggestedPriceCent}</td>
      <td>${r.stockUpdatedAt ?? ""}</td>
    </tr>
  `).join("");
}

async function loadPriceHistory() {
  const priceRows = await apiGet(`/api/wms/pricing/price-changes?limit=200`);
  $("#tbl-price-history").innerHTML = priceRows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.sku}</td>
      <td>${r.oldPriceCent}</td>
      <td>${r.newPriceCent}</td>
      <td>${r.operator}</td>
      <td>${r.createdAt ?? ""}</td>
    </tr>
  `).join("");

  const costRows = await apiGet(`/api/wms/pricing/cost-changes?limit=200`);
  $("#tbl-cost-history").innerHTML = costRows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.sku}</td>
      <td>${r.oldCostCent}</td>
      <td>${r.newCostCent}</td>
      <td>${r.operator}</td>
      <td>${r.createdAt ?? ""}</td>
    </tr>
  `).join("");
}

async function loadNewProducts() {
  const platformId = $("#sel-platform").value;
  const rows = await apiGet(`/api/wms/catalog/new-products?platformId=${encodeURIComponent(platformId)}`);
  $("#tbl-new-products").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td>${r.candidateCode}</td>
      <td>${r.name}</td>
      <td><span class="badge text-bg-secondary">${r.status}</span></td>
      <td>${r.expectedCostCent}</td>
      <td>${r.expectedPriceCent}</td>
      <td>${r.updatedAt ?? ""}</td>
      <td class="d-flex gap-2 flex-wrap">
        ${wmsCanWrite ? `
        <button class="btn btn-sm btn-outline-primary" data-np-status="${r.id}" data-val="APPROVED">通过</button>
        <button class="btn btn-sm btn-outline-danger" data-np-status="${r.id}" data-val="REJECTED">驳回</button>
        <button class="btn btn-sm btn-outline-success" data-np-status="${r.id}" data-val="ONBOARDED">已上架</button>` : '<span class="text-secondary small">只读</span>'}
      </td>
    </tr>
  `).join("");

  $("#tbl-new-products").querySelectorAll("button[data-np-status]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-np-status");
    const status = b.getAttribute("data-val");
    const cur = await apiGet(`/api/wms/catalog/new-products/${id}`);
    await apiPutJson(`/api/wms/catalog/new-products/${id}`, {
      name: cur.name,
      category: cur.category,
      expectedCostCent: cur.expectedCostCent,
      expectedPriceCent: cur.expectedPriceCent,
      remark: cur.remark,
      status
    });
    await loadNewProducts();
  }));
}

async function loadPurchaseOrders() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#sel-store").value;
  const rows = await apiGet(`/api/wms/procurement/purchase-orders?platformId=${platformId}&storeId=${storeId}&limit=50`);
  $("#tbl-po").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td><a href="#" data-po-detail="${r.id}">${r.orderNo}</a></td>
      <td>${r.warehouseCode}</td>
      <td><span class="badge text-bg-secondary">${r.status}</span></td>
      <td>${r.supplierName ?? ""}</td>
      <td>${r.createdAt ?? ""}</td>
      <td class="d-flex gap-2 flex-wrap">
        ${wmsCanWrite ? `
        <button class="btn btn-sm btn-outline-primary" data-po-approve="${r.id}">审核</button>
        <button class="btn btn-sm btn-outline-primary" data-po-ship="${r.id}">发货</button>
        <button class="btn btn-sm btn-outline-success" data-po-receive="${r.id}">收货入库</button>` : '<span class="text-secondary small">只读</span>'}
      </td>
    </tr>
  `).join("");

  $("#po-detail-box").classList.add("d-none");
  $("#tbl-po").querySelectorAll("a[data-po-detail]").forEach(a => {
    a.addEventListener("click", async (e) => {
      e.preventDefault();
      const id = a.getAttribute("data-po-detail");
      const detail = await apiGet(`/api/wms/procurement/purchase-orders/${id}`);
      $("#po-detail").textContent = JSON.stringify(detail, null, 2);
      $("#po-detail-box").classList.remove("d-none");
    });
  });

  $("#tbl-po").querySelectorAll("button[data-po-approve]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-po-approve");
    await apiPostJson(`/api/wms/procurement/purchase-orders/${id}:approve`, {});
    await loadPurchaseOrders();
  }));
  $("#tbl-po").querySelectorAll("button[data-po-ship]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-po-ship");
    await apiPostJson(`/api/wms/procurement/purchase-orders/${id}:ship`, {});
    await loadPurchaseOrders();
  }));
  $("#tbl-po").querySelectorAll("button[data-po-receive]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-po-receive");
    await apiPostJson(`/api/wms/procurement/purchase-orders/${id}:receive`, {});
    await loadPurchaseOrders();
    await loadWarehouseInventory();
  }));
}

async function loadTransferOrders() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#sel-store").value;
  const rows = await apiGet(`/api/wms/transfer/orders?platformId=${platformId}&storeId=${storeId}&limit=50`);
  $("#tbl-to").innerHTML = rows.map(r => `
    <tr>
      <td>${r.id}</td>
      <td><a href="#" data-to-detail="${r.id}">${r.orderNo}</a></td>
      <td>${r.fromWarehouseCode}</td>
      <td>${r.toWarehouseCode}</td>
      <td><span class="badge text-bg-secondary">${r.status}</span></td>
      <td>${r.createdAt ?? ""}</td>
      <td class="d-flex gap-2 flex-wrap">
        ${wmsCanWrite ? `
        <button class="btn btn-sm btn-outline-primary" data-to-ship="${r.id}">发出</button>
        <button class="btn btn-sm btn-outline-success" data-to-receive="${r.id}">收货</button>` : '<span class="text-secondary small">只读</span>'}
      </td>
    </tr>
  `).join("");

  $("#to-detail-box").classList.add("d-none");
  $("#tbl-to").querySelectorAll("a[data-to-detail]").forEach(a => {
    a.addEventListener("click", async (e) => {
      e.preventDefault();
      const id = a.getAttribute("data-to-detail");
      const detail = await apiGet(`/api/wms/transfer/orders/${id}`);
      $("#to-detail").textContent = JSON.stringify(detail, null, 2);
      $("#to-detail-box").classList.remove("d-none");
    });
  });

  $("#tbl-to").querySelectorAll("button[data-to-ship]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-to-ship");
    await apiPostJson(`/api/wms/transfer/orders/${id}:ship`, {});
    await loadTransferOrders();
    await loadWarehouseInventory();
  }));
  $("#tbl-to").querySelectorAll("button[data-to-receive]").forEach(b => b.addEventListener("click", async () => {
    const id = b.getAttribute("data-to-receive");
    await apiPostJson(`/api/wms/transfer/orders/${id}:receive`, {});
    await loadTransferOrders();
    await loadWarehouseInventory();
  }));
}

async function loadSlowMoving() {
  const rows = await apiGet("/api/wms/analytics/slow-moving?limit=80");
  $("#tbl-slow").innerHTML = rows.map((r, idx) => `
    <tr>
      <td>${idx + 1}</td>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>${r.qty30d}</td>
      <td>${r.stockTotalQty}</td>
      <td>${r.stockAvailableQty}</td>
      <td>${r.stockInTransitQty}</td>
      <td>${pct(r.sellThroughRate30d)}</td>
      <td>${r.daysOfSupply ?? ""}</td>
    </tr>
  `).join("");
}

async function loadNegativeProfit() {
  const rows = await apiGet("/api/wms/analytics/negative-profit/skus?limit=80");
  $("#tbl-loss").innerHTML = rows.map((r, idx) => `
    <tr>
      <td>${idx + 1}</td>
      <td>${r.sku}</td>
      <td>${r.title}</td>
      <td>¥${moneyCentToYuan(r.costCent)}</td>
      <td>¥${moneyCentToYuan(r.salePriceCent)}</td>
      <td class="text-danger">¥${moneyCentToYuan(r.profitPerUnitCent)}</td>
      <td class="text-danger">${pct(r.profitRate)}</td>
    </tr>
  `).join("");
}

async function loadOps() {
  const platformId = $("#sel-platform").value;
  const storeId = $("#ops-store").value;
  const from = $("#ops-from").value;
  const to = $("#ops-to").value;
  if (!platformId || !storeId || !from || !to) return;

  const [kpis, cash, ads, funnel] = await Promise.all([
    apiGet(`/api/wms/ops/kpi?platformId=${platformId}&storeId=${storeId}&from=${from}&to=${to}`),
    apiGet(`/api/wms/ops/cash-flow/daily?platformId=${platformId}&storeId=${storeId}&from=${from}&to=${to}`),
    apiGet(`/api/wms/ops/ad-spend/daily?platformId=${platformId}&storeId=${storeId}&from=${from}&to=${to}`),
    apiGet(`/api/wms/ops/sales-funnel/daily?from=${from}&to=${to}`)
  ]);

  const sum = (arr, f) => arr.reduce((a, x) => a + (f(x) ?? 0), 0);
  const sumGmv = sum(kpis, x => x.gmvCent);
  const sumProfit = sum(kpis, x => x.grossProfitCent);
  const sumOrders = sum(kpis, x => x.orderCnt);
  const sumNet = sum(cash, x => x.netCent);
  $("#ops-sum-gmv").textContent = `¥${moneyCentToYuan(sumGmv)}`;
  $("#ops-sum-profit").textContent = `¥${moneyCentToYuan(sumProfit)}`;
  $("#ops-sum-orders").textContent = `${sumOrders}`;
  $("#ops-sum-net").textContent = `¥${moneyCentToYuan(sumNet)}`;

  $("#tbl-ops-kpi").innerHTML = kpis.map(r => `
    <tr>
      <td>${r.day}</td>
      <td>¥${moneyCentToYuan(r.gmvCent)}</td>
      <td>${r.orderCnt}</td>
      <td>¥${moneyCentToYuan(r.refundCent)}</td>
      <td>¥${moneyCentToYuan(r.adCostCent)}</td>
      <td>¥${moneyCentToYuan(r.grossProfitCent)}</td>
      <td>${pct(r.grossProfitRate)}</td>
    </tr>
  `).join("");

  $("#tbl-ops-cash").innerHTML = cash.map(r => `
    <tr>
      <td>${r.day}</td>
      <td>¥${moneyCentToYuan(r.incomeCent)}</td>
      <td>¥${moneyCentToYuan(r.expenseCent)}</td>
      <td>¥${moneyCentToYuan(r.netCent)}</td>
    </tr>
  `).join("");

  $("#tbl-ops-ads").innerHTML = ads.map(r => `
    <tr>
      <td>${r.day}</td>
      <td>¥${moneyCentToYuan(r.spendCent)}</td>
      <td>${r.impressions}</td>
      <td>${r.clicks}</td>
    </tr>
  `).join("");

  const gmvLabels = kpis.map(x => x.day);
  const gmvData = kpis.map(x => x.gmvCent / 100);
  const netByDay = new Map(cash.map(x => [x.day, x.netCent / 100]));
  const netLabels = cash.map(x => x.day);
  const netData = cash.map(x => x.netCent / 100);

  const gmvCtx = $("#ops-gmv");
  if (opsGmvChart) opsGmvChart.destroy();
  opsGmvChart = new Chart(gmvCtx, {
    type: "line",
    data: { labels: gmvLabels, datasets: [{ label: "GMV(元)", data: gmvData, borderWidth: 2, tension: 0.25 }] },
    options: { responsive: true, scales: { y: { beginAtZero: true } } }
  });

  const netCtx = $("#ops-net");
  if (opsNetChart) opsNetChart.destroy();
  opsNetChart = new Chart(netCtx, {
    type: "line",
    data: { labels: netLabels, datasets: [{ label: "净额(元)", data: netData, borderWidth: 2, tension: 0.25 }] },
    options: { responsive: true }
  });

  const adsLabels = ads.map(x => x.day);
  const adsData = ads.map(x => x.spendCent / 100);
  const adsCtx = $("#ops-ads");
  if (adsCtx) {
    if (opsAdsChart) opsAdsChart.destroy();
    opsAdsChart = new Chart(adsCtx, {
      type: "line",
      data: { labels: adsLabels, datasets: [{ label: "广告支出(元)", data: adsData, borderWidth: 2, tension: 0.25 }] },
      options: { responsive: true, scales: { y: { beginAtZero: true } } }
    });
  }

  const funLabels = funnel.map(x => x.day);
  const created = funnel.map(x => x.createdCnt);
  const paid = funnel.map(x => x.paidCnt);
  const shipped = funnel.map(x => x.shippedCnt);
  const delivered = funnel.map(x => x.deliveredCnt);
  const funCtx = $("#ops-funnel");
  if (funCtx) {
    if (opsFunnelChart) opsFunnelChart.destroy();
    opsFunnelChart = new Chart(funCtx, {
      type: "line",
      data: {
        labels: funLabels,
        datasets: [
          { label: "创建", data: created, borderWidth: 2, tension: 0.25 },
          { label: "支付", data: paid, borderWidth: 2, tension: 0.25 },
          { label: "发货", data: shipped, borderWidth: 2, tension: 0.25 },
          { label: "签收", data: delivered, borderWidth: 2, tension: 0.25 },
        ]
      },
      options: { responsive: true }
    });
  }
}

function setOpsTab(tab) {
  $$("#ops-tabs .nav-link").forEach(b => b.classList.toggle("active", b.dataset.tab === tab));
  $("#ops-panel-kpi").classList.toggle("d-none", tab !== "kpi");
  $("#ops-panel-ads").classList.toggle("d-none", tab !== "ads");
  $("#ops-panel-cash").classList.toggle("d-none", tab !== "cash");
  $("#ops-panel-funnel").classList.toggle("d-none", tab !== "funnel");
  $("#ops-table-kpi").classList.toggle("d-none", tab !== "kpi");
  $("#ops-table-ads").classList.toggle("d-none", tab !== "ads");
  $("#ops-table-cash").classList.toggle("d-none", tab !== "cash");
}

function bindNav() {
  const nav = $("#top-app-nav");
  if (!nav) return;
  nav.addEventListener("click", async (e) => {
    const t = e.target.closest("[data-view]");
    if (!t || !nav.contains(t)) return;
    if (t.style.display === "none") return;
    const view = t.dataset.view;
    setActiveView(view);
    await logDirectoryUsage(view);
    try {
      if (view === "products") await loadProducts();
      if (view === "variants") await loadVariants();
      if (view === "stores") await loadStores();
      if (view === "store-kpi") { await loadOpsStoresFromCurrentPlatform(); await loadOps(); }
      if (view === "inventory-change") {
        setInvTab("pending");
        await loadInvPending();
      }
      if (view === "warehouse-list") await loadWarehouseList();
      if (view === "warehouse-snapshot") { await loadWarehousesSelect(); await loadWarehouseInventory(); }
      if (view === "rank-sales") await loadRankSales();
      if (view === "rank-profit") await loadRankProfit();
      if (view === "profit-table") { await loadStoreSelect("#profit-store"); await loadProfitTable(); }
      if (view === "price-todos") { await loadStoreSelect("#todo-store"); await loadPriceTodos(); }
      if (view === "price-history") await loadPriceHistory();
      if (view === "new-products") await loadNewProducts();
      if (view === "slow") await loadSlowMoving();
      if (view === "loss") await loadNegativeProfit();
      if (view === "orders") { await loadStoreSelect("#orders-store"); await loadOrders(); }
      if (view === "bi") await loadBi();
      if (view === "sql") await loadSqlSnippets();
      if (view === "admin-usage") await loadUsageTopDirectories();
      if (view === "account-admin") await loadAccountAdmin();
      if (view === "crud-admin") await loadCrudAdmin();
    } catch (err) {
      alert(err.message || String(err));
    }
  });
}

async function bootstrap() {
  bindNav();
  initDates();
  bindAccountAdminTabs();
  bindAccountAdminModals();
  bindCrudAdmin();
  try {
    await loadPlatforms();
    await initPermissions();

    if (isViewAllowed("products")) await loadProducts();
    if (isViewAllowed("variants") || isViewAllowed("bi")) await loadVariants();
    if (isViewAllowed("stores") || isViewAllowed("bi")) await loadStores();

    if (isViewAllowed("warehouse-list")) await loadWarehouseList();
    if (isViewAllowed("warehouse-snapshot")) {
      await loadWarehousesSelect();
      await loadWarehouseInventory();
    }

    if (isViewAllowed("rank-sales")) await loadRankSales();
    if (isViewAllowed("rank-profit")) await loadRankProfit();
    if (isViewAllowed("slow")) await loadSlowMoving();
    if (isViewAllowed("loss")) await loadNegativeProfit();

    if (isViewAllowed("inventory-change")) {
      setInvTab("pending");
      await loadInvPending();
    }

    if (isViewAllowed("store-kpi")) {
      await loadOpsStoresFromCurrentPlatform();
      await loadOps();
    }

    if (isViewAllowed("orders")) {
      await loadStoreSelect("#orders-store");
      await loadOrders();
    }

    if (isViewAllowed("profit-table")) {
      await loadStoreSelect("#profit-store");
      await loadProfitTable();
      await calcProfit();
    }

    if (isViewAllowed("price-todos")) {
      await loadStoreSelect("#todo-store");
      await loadPriceTodos();
    }

    if (isViewAllowed("price-history")) await loadPriceHistory();
    if (isViewAllowed("new-products")) await loadNewProducts();
    if (isViewAllowed("bi")) await loadBi();
    if (isViewAllowed("sql")) await loadSqlSnippets();

    if (isViewAllowed("admin-usage")) await loadUsageTopDirectories();
    if (isViewAllowed("account-admin")) await loadAccountAdmin();
    if (isViewAllowed("crud-admin")) await loadCrudAdmin();
  } catch (e) {
    // 第一次启动可能还没建表/没数据，给个轻提示即可
    console.warn(e);
  }
}

$("#btn-refresh-products").addEventListener("click", () => loadProducts().catch(e => alert(e.message)));
$("#btn-refresh-variants").addEventListener("click", () => loadVariants().catch(e => alert(e.message)));
$("#btn-refresh-stores").addEventListener("click", () => loadStores().catch(e => alert(e.message)));
$("#btn-refresh-inventory").addEventListener("click", () => loadInventory().catch(e => alert(e.message)));
$("#btn-refresh-bi").addEventListener("click", () => loadBi().catch(e => alert(e.message)));
$("#btn-refresh-warehouse-list").addEventListener("click", () => loadWarehouseList().catch(e => alert(e.message)));
$("#btn-refresh-warehouse").addEventListener("click", () => loadWarehouseInventory().catch(e => alert(e.message)));
$("#btn-refresh-rank-sales").addEventListener("click", () => loadRankSales().catch(e => alert(e.message)));
$("#btn-refresh-rank-profit").addEventListener("click", () => loadRankProfit().catch(e => alert(e.message)));
$("#btn-refresh-ops").addEventListener("click", () => loadOps().catch(e => alert(e.message)));
// 采购单/调拨单已合并到“库存变动”页签，无单独刷新按钮
$("#btn-refresh-slow").addEventListener("click", () => loadSlowMoving().catch(e => alert(e.message)));
$("#btn-refresh-loss").addEventListener("click", () => loadNegativeProfit().catch(e => alert(e.message)));
$("#btn-refresh-orders").addEventListener("click", () => loadOrders().catch(e => alert(e.message)));
$("#btn-refresh-profit-table").addEventListener("click", () => loadProfitTable().catch(e => alert(e.message)));
$("#btn-refresh-price-todos").addEventListener("click", () => loadPriceTodos().catch(e => alert(e.message)));
$("#btn-refresh-price-history").addEventListener("click", () => loadPriceHistory().catch(e => alert(e.message)));
$("#btn-refresh-new-products").addEventListener("click", () => loadNewProducts().catch(e => alert(e.message)));
$("#btn-refresh-admin-usage").addEventListener("click", () => loadUsageTopDirectories().catch(e => alert(e.message)));
$("#btn-refresh-accounts").addEventListener("click", () => loadAccountAdmin().catch(e => alert(e.message)));

$("#btn-calc-profit").addEventListener("click", () => calcProfit().catch(e => alert(e.message)));
$("#calc-price-cent").addEventListener("change", () => calcProfit().catch(e => alert(e.message)));
$("#calc-cost-cent").addEventListener("change", () => calcProfit().catch(e => alert(e.message)));

$("#sel-platform").addEventListener("change", async () => {
  try {
    if (isViewAllowed("stores") || isViewAllowed("bi")) await loadStores();
    if (isViewAllowed("store-kpi")) {
      await loadOpsStoresFromCurrentPlatform();
      await loadOps();
    }
    if (isViewAllowed("orders")) {
      await loadStoreSelect("#orders-store");
      await loadOrders();
    }
    if (isViewAllowed("profit-table")) {
      await loadStoreSelect("#profit-store");
      await loadProfitTable();
      await calcProfit();
    }
    if (isViewAllowed("price-todos")) {
      await loadStoreSelect("#todo-store");
      await loadPriceTodos();
    }
    if (isViewAllowed("price-history")) await loadPriceHistory();
    if (isViewAllowed("new-products")) await loadNewProducts();
    if (isViewAllowed("bi")) await loadBi();
    if (isViewAllowed("admin-usage")) await loadUsageTopDirectories();
  } catch (e) {
    alert(e.message || String(e));
  }
});

$("#orders-store").addEventListener("change", async () => {
  try {
    await loadOrders();
  } catch (e) {
    alert(e.message || String(e));
  }
});

$("#orders-from").addEventListener("change", async () => loadOrders().catch(e => alert(e.message || String(e))));
$("#orders-to").addEventListener("change", async () => loadOrders().catch(e => alert(e.message || String(e))));
$("#orders-status").addEventListener("change", async () => loadOrders().catch(e => alert(e.message || String(e))));

$("#profit-store").addEventListener("change", async () => loadProfitTable().catch(e => alert(e.message || String(e))));
$("#todo-store").addEventListener("change", async () => loadPriceTodos().catch(e => alert(e.message || String(e))));
$("#todo-min-days").addEventListener("change", async () => loadPriceTodos().catch(e => alert(e.message || String(e))));

$("#btn-np-create").addEventListener("click", async () => {
  try {
    const platformId = $("#sel-platform").value;
    const candidateCode = $("#np-code").value.trim();
    const name = $("#np-name").value.trim();
    const category = $("#np-category").value.trim();
    const expectedCostCent = Number($("#np-cost").value || 0);
    const expectedPriceCent = Number($("#np-price").value || 0);
    const remark = $("#np-remark").value.trim();
    if (!candidateCode || !name) throw new Error("请填写候选编码和名称");
    await apiPostJson(`/api/wms/catalog/new-products`, {
      platformId,
      candidateCode,
      name,
      category: category || null,
      expectedCostCent,
      expectedPriceCent,
      remark: remark || null,
      createdBy: "web"
    });
    $("#np-code").value = "";
    $("#np-name").value = "";
    $("#np-category").value = "";
    $("#np-cost").value = "";
    $("#np-price").value = "";
    $("#np-remark").value = "";
    await loadNewProducts();
  } catch (e) {
    alert(e.message || String(e));
  }
});

// 库存变动页签切换
if (document.getElementById("inv-change-tabs")) {
  $$("#inv-change-tabs .nav-link").forEach(btn => {
    btn.addEventListener("click", async () => {
      const tab = btn.dataset.tab;
      setInvTab(tab);
      try {
        if (tab === "pending") await loadInvPending();
        if (tab === "allocated") await loadInvAllocated();
        if (tab === "sync") await loadInvSyncLogs();
        if (tab === "clear") await loadInvClearRecords();
        if (tab === "snapshot") await loadInventory();
        if (tab === "purchase") await loadPurchaseOrders();
        if (tab === "transfer") await loadTransferOrders();
      } catch (e) {
        alert(e.message || String(e));
      }
    });
  });
}

$("#sel-warehouse").addEventListener("change", async () => {
  try {
    await loadWarehouseInventory();
  } catch (e) {
    alert(e.message || String(e));
  }
});

$("#ops-store").addEventListener("change", async () => {
  try {
    await loadOps();
  } catch (e) {
    alert(e.message || String(e));
  }
});

// 运营看板页签切换
if (document.getElementById("ops-tabs")) {
  $$("#ops-tabs .nav-link").forEach(btn => {
    btn.addEventListener("click", async () => {
      const tab = btn.dataset.tab;
      setOpsTab(tab);
      // 数据由 loadOps 一次性拉取并渲染，这里只切换展示
    });
  });
  setOpsTab("kpi");
}

bootstrap();

