import { useCallback, useEffect, useState } from "react";

const API_URL = "http://localhost:8080/app";
const ACCESS_TOKEN_KEY = "book_store_access_token";
const AUTH_USER_KEY = "book_store_auth_user";
const STORE_LOGO_URL = "/store-logo.png";

const DEFAULT_COVER =
  "data:image/svg+xml;utf8," +
  encodeURIComponent(`
  <svg xmlns="http://www.w3.org/2000/svg" width="360" height="520" viewBox="0 0 360 520">
    <defs>
      <linearGradient id="g" x1="0" x2="1" y1="0" y2="1">
        <stop stop-color="#f1d1b9"/>
        <stop offset="1" stop-color="#f8eadf"/>
      </linearGradient>
    </defs>
    <rect width="360" height="520" rx="18" fill="url(#g)"/>
    <rect x="48" y="62" width="264" height="382" rx="18" fill="#fff8ef" opacity="0.9"/>
    <path d="M103 174h154M103 220h154M103 266h102" stroke="#4e3126" stroke-width="14" stroke-linecap="round"/>
    <circle cx="180" cy="350" r="48" fill="#d9853b" opacity="0.88"/>
    <text x="180" y="468" text-anchor="middle" font-size="25" font-family="Georgia" fill="#4e3126" font-weight="700">BOOK</text>
  </svg>`);

const bookFilterDefaults = {
  type: "BOOK",
  direction: "ASC",
  field: "TITLE",
  availableOnly: false,
};

const orderFilterDefaults = {
  type: "ORDER",
  filtered: false,
  direction: "ASC",
  field: "PRICE",
  from: "",
  to: "",
};

const filterDictionaries = {
  type: ["BOOK", "STALE_BOOK"],
  orderType: ["ORDER", "COMPLETED_ORDER"],
  requestType: ["REQUEST"],
  direction: ["ASC", "DESC"],
  bookField: ["TITLE", "PRICE", "AVAILABILITY", "DELIVERY_DATE", "COUNT"],
  orderField: ["PRICE", "COMPLETION_DATE", "STATUS", "TITLE"],
};

const optionLabels = {
  BOOK: "Книги",
  STALE_BOOK: "Архив",
  ORDER: "Заказы",
  COMPLETED_ORDER: "Завершённые",
  REQUEST: "Заявки",
  ASC: "Возрастанию",
  DESC: "Убыванию",
  TITLE: "Названию",
  PRICE: "Цене",
  AVAILABILITY: "Наличию",
  DELIVERY_DATE: "Доставке",
  COMPLETION_DATE: "Завершению",
  STATUS: "Статусу",
  COUNT: "Количеству",
};

function App() {
  const [page, setPage] = useState("catalog");
  const [pagePayload, setPagePayload] = useState(null);
  const [accessToken, setAccessToken] = useState(() => localStorage.getItem(ACCESS_TOKEN_KEY) || "");
  const [currentUser, setCurrentUser] = useState(() => getStoredUser() || getUserFromToken(localStorage.getItem(ACCESS_TOKEN_KEY) || ""));
  const [authRequired, setAuthRequired] = useState(false);
  const [toast, setToast] = useState(null);
  const [searchQuery, setSearchQuery] = useState("");

  const isAdmin = hasRole(currentUser, "ADMIN");

  const notify = useCallback((message, variant = "info") => {
    setToast({ message, variant, id: Date.now() });
    window.clearTimeout(window.__bookStoreToastTimer);
    window.__bookStoreToastTimer = window.setTimeout(() => setToast(null), 3600);
  }, []);

  const saveAccessToken = useCallback((token, userFromResponse = null) => {
    if (!token) {
      localStorage.removeItem(ACCESS_TOKEN_KEY);
      localStorage.removeItem(AUTH_USER_KEY);
      setAccessToken("");
      setCurrentUser(null);
      return;
    }

    const nextUser = userFromResponse || getUserFromToken(token);

    localStorage.setItem(ACCESS_TOKEN_KEY, token);
    if (nextUser) {
      localStorage.setItem(AUTH_USER_KEY, JSON.stringify(nextUser));
    }
    setAccessToken(token);
    setCurrentUser(nextUser);
    setAuthRequired(false);
  }, []);

  const apiFetch = useCallback(
    async (path, options = {}, retry = true) => {
      const headers = new Headers(options.headers || {});
      const hasBody = options.body !== undefined && options.body !== null;

      if (hasBody && !(options.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
      }

      const token = localStorage.getItem(ACCESS_TOKEN_KEY);
      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }

      const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers,
        credentials: "include",
      });

      if (response.status === 401 && retry) {
        const refreshed = await refreshAccessToken(saveAccessToken);
        if (refreshed) {
          return apiFetch(path, options, false);
        }

        setAuthRequired(true);
        throw new ApiError(401, "Нужно войти в аккаунт", "UNAUTHORIZED");
      }

      const text = await response.text();
      const data = text ? safeJson(text) : null;

      if (!response.ok) {
        const message = data?.message || data?.error || `Ошибка ${response.status}`;
        const errorCode = data?.errorCode || data?.code || data?.error;

        if (response.status === 401 || response.status === 403 || data?.error === "Invalid or expired token") {
          setAuthRequired(true);
        }

        throw new ApiError(response.status, message, errorCode, data);
      }

      return data;
    },
    [saveAccessToken]
  );

  function go(nextPage, payload = null) {
    setPage(nextPage);
    setPagePayload(payload);
  }

  function handleSearch(query) {
    setSearchQuery(query);
    go("catalog");
  }

  return (
    <div className="app">
      <style>{styles}</style>

      <Header
        page={page}
        go={go}
        isAuthorized={Boolean(accessToken)}
        isAdmin={isAdmin}
        user={currentUser}
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        onSearch={handleSearch}
        onLogout={() => {
          saveAccessToken("");
          setAuthRequired(true);
          notify("Вы вышли из аккаунта", "info");
        }}
      />

      <main className="shell">
        {isAdmin ? (
          <AdminPage apiFetch={apiFetch} notify={notify} />
        ) : (
          <>
            {page === "login" && (
              <AuthPage
                saveAccessToken={saveAccessToken}
                apiFetch={apiFetch}
                notify={notify}
                onSuccess={(user) => go(hasRole(user, "ADMIN") ? "admin" : "catalog")}
              />
            )}
            {page === "catalog" && (
              <CatalogPage apiFetch={apiFetch} notify={notify} go={go} isAuthorized={Boolean(accessToken)} searchQuery={searchQuery} />
            )}

            {page === "orders" && (
              <OrdersPage apiFetch={apiFetch} authRequired={!accessToken || authRequired} notify={notify} go={go} />
            )}

            {page === "cart" && (
              <CartPage apiFetch={apiFetch} notify={notify} go={go} authRequired={!accessToken || authRequired} />
            )}

            {page === "profile" && (
              <ProfilePage
                apiFetch={apiFetch}
                notify={notify}
                authRequired={!accessToken || authRequired}
                user={currentUser}
                onLogout={() => {
                  saveAccessToken("");
                  setAuthRequired(true);
                  notify("Вы вышли из аккаунта", "info");
                  go("login");
                }}
              />
            )}

            {page === "payment" && (
              <PaymentPage apiFetch={apiFetch} notify={notify} initial={pagePayload} go={go} />
            )}
          </>
        )}
      </main>

      {toast && <Toast toast={toast} />}
    </div>
  );
}

function Icon({ name }) {
  const common = {
    width: 20,
    height: 20,
    viewBox: "0 0 24 24",
    fill: "none",
    stroke: "currentColor",
    strokeWidth: 1.9,
    strokeLinecap: "round",
    strokeLinejoin: "round",
    "aria-hidden": true,
    className: "navIcon",
  };

  const icons = {
    catalog: (
      <svg {...common}>
        <path d="M4 19.5V6.75A2.75 2.75 0 0 1 6.75 4H20v13.5H6.75A2.75 2.75 0 0 0 4 20.25" />
        <path d="M8 7h8" />
        <path d="M8 10h6" />
      </svg>
    ),
    orders: (
      <svg {...common}>
        <path d="M7 4h10l2 3v13H5V7l2-3Z" />
        <path d="M7 7h10" />
        <path d="M9 12h6" />
        <path d="M9 16h4" />
      </svg>
    ),
    profile: (
      <svg {...common}>
        <path d="M20 21a8 8 0 0 0-16 0" />
        <circle cx="12" cy="8" r="4" />
      </svg>
    ),
    cart: (
      <svg {...common}>
        <path d="M6 6h15l-1.6 8.2a2 2 0 0 1-2 1.6H9.1a2 2 0 0 1-2-1.7L5.7 3.8H3" />
        <circle cx="9" cy="20" r="1.4" />
        <circle cx="18" cy="20" r="1.4" />
      </svg>
    ),
    admin: (
      <svg {...common}>
        <path d="M12 3l7 3v5c0 4.5-2.9 8.4-7 10-4.1-1.6-7-5.5-7-10V6l7-3Z" />
        <path d="M9.5 12l1.7 1.7 3.7-4" />
      </svg>
    ),
    search: (
      <svg {...common}>
        <circle cx="11" cy="11" r="7" />
        <path d="M20 20l-3.5-3.5" />
      </svg>
    ),
  };

  return icons[name] || null;
}

function Header({ page, go, isAuthorized, isAdmin, user, searchQuery, setSearchQuery, onSearch, onLogout }) {
  const nav = isAdmin
    ? [{ id: "admin", label: "Админ", icon: "admin" }]
    : [
        { id: "catalog", label: "Каталог", icon: "catalog" },
        { id: "orders", label: "Заказы", icon: "orders" },
        { id: "profile", label: "Профиль", icon: "profile" },
      ];

  return (
    <header className="topbar">
      <button className="brand" onClick={() => go(isAdmin ? "admin" : "catalog")}>
        <img className="brandPhoto" src={STORE_LOGO_URL} alt="" />
        <span className="brandText">Время<br />ПриклюЧтений</span>
      </button>

      {!isAdmin && (
        <form className="searchBox" onSubmit={(event) => { event.preventDefault(); onSearch(searchQuery); }}>
          <input
            placeholder="поиск по названию или автору"
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
          />
          <button type="submit" aria-label="Найти"><Icon name="search" /></button>
        </form>
      )}

      <nav className="nav">
        {nav.map((item) => (
          <button
            key={item.id}
            className={page === item.id || isAdmin ? "navItem active" : "navItem"}
            onClick={() => go(item.id)}
          >
            <Icon name={item.icon} />
            {item.label}
          </button>
        ))}
      </nav>

      {!isAdmin && (
        <button className={page === "cart" ? "navItem active" : "navItem"} onClick={() => go("cart")}>
          <Icon name="cart" />
          Корзина
        </button>
      )}

      {isAuthorized ? (
        isAdmin ? (
          <button className="loginBtn" onClick={onLogout}>Выйти</button>
        ) : (
          <button className="loginBtn" onClick={() => go("profile")}>{user?.email || "Профиль"}</button>
        )
      ) : (
        <button className="loginBtn" onClick={() => go("login")}>Войти</button>
      )}
    </header>
  );
}
function AuthPage({ saveAccessToken, apiFetch, notify, onSuccess }) {
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState({ email: "", password: "" });
  const [loading, setLoading] = useState(false);

  async function submit(event) {
    event.preventDefault();
    setLoading(true);

    try {
      const data = await apiFetch(`/auth/${mode}`, {
        method: "POST",
        body: JSON.stringify(form),
      }, false);

      const user = buildUserFromAuthResponse(data);
      saveAccessToken(data?.accessToken, user);
      notify(mode === "login" ? "Вход выполнен" : "Регистрация выполнена", "success");
      onSuccess?.(user);
    } catch (error) {
      notify(error.message || "Не удалось авторизоваться", "error");
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="authPanel">
      <div>
        <h2>{mode === "login" ? "Вход" : "Регистрация"}</h2>
        <p>После входа станут доступны заказы, корзина, оплата и привязанные платёжные реквизиты</p>
      </div>

      <form className="authForm" onSubmit={submit}>
        <input type="email" placeholder="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
        <input type="password" placeholder="пароль" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
        <button className="primaryBtn" disabled={loading}>{loading ? "Отправка..." : mode === "login" ? "Войти" : "Зарегистрироваться"}</button>
        <button type="button" className="linkBtn" onClick={() => setMode(mode === "login" ? "register" : "login")}>{mode === "login" ? "Создать аккаунт" : "Уже есть аккаунт"}</button>
      </form>
    </section>
  );
}

function CatalogPage({ apiFetch, notify, go, isAuthorized, searchQuery }) {
  const [books, setBooks] = useState([]);
  const [selectedBook, setSelectedBook] = useState(null);
  const [filterForm, setFilterForm] = useState(bookFilterDefaults);
  const [appliedFilters, setAppliedFilters] = useState(bookFilterDefaults);
  const [heroHiddenByFilters, setHeroHiddenByFilters] = useState(false);
  const [loading, setLoading] = useState(false);

  const loadBooks = useCallback(async (filtersForRequest = bookFilterDefaults) => {
    setLoading(true);
    try {
      const data = await apiFetch(`/books${toQuery(cleanBookFilters(filtersForRequest))}`);
      setBooks(data?.books || []);
    } catch (error) {
      notify(error.message || "Не удалось загрузить каталог", "error");
    } finally {
      setLoading(false);
    }
  }, [apiFetch, notify]);

  useEffect(() => {
    loadBooks(bookFilterDefaults);
  }, [loadBooks]);

  function applyFilters() {
    setAppliedFilters(filterForm);
    setHeroHiddenByFilters(true);
    loadBooks(filterForm);
  }

  async function openBook(isbn) {
    try {
      setSelectedBook(await apiFetch(`/books/${encodeURIComponent(isbn)}`));
    } catch (error) {
      notify(error.message || "Не удалось открыть книгу", "error");
    }
  }

  const visibleBooks = books.filter((book) => {
    if (appliedFilters.availableOnly && !book.available) return false;
    return matchesBookSearch(book, searchQuery);
  });

  const shouldShowCatalogHero = !normalizeSearch(searchQuery) && !heroHiddenByFilters;

  async function addToBasket(book) {
    try {
      await apiFetch(`/books/basket/${encodeURIComponent(book.isbn)}/add`, { method: "POST" });
      notify("Книга добавлена в корзину", "success");
    } catch (error) {
      notify(error.message || "Не удалось добавить книгу в корзину", "error");
    }
  }

  async function orderNow(book) {
    try {
      const order = await apiFetch(`/orders/${encodeURIComponent(book.isbn)}`, {
        method: "POST",
      });
      notify("Заказ создан, можно перейти к оплате", "success");
      go("payment", { orderId: order?.orderId, paymentId: order?.paymentId, source: "book" });
    } catch (error) {
      notify(error.message || "Не удалось создать заказ", "error");
    }
  }

  return (
    <section className="catalogLayout">
      <aside className="leftFilters">
        <FilterPanel filters={filterForm} setFilters={setFilterForm} onApply={applyFilters} />
      </aside>

      <div className="contentPanel">
        {shouldShowCatalogHero && (
          <section className="catalogHero">
            <div className="eyebrow">Новинки и классика книжного мира</div>
            <h1>Любить чтение — это обменивать часы скуки, неизбежные в жизни, на часы большого наслаждения</h1>
            <p>Выбирайте книги, проверяйте наличие и начинайте новое приключение</p>
            <div className="heroStats">
              <span>{visibleBooks.length} товаров</span>
              <span>Поиск по названию и автору</span>
              <span>Корзина и оплата после входа</span>
            </div>
          </section>
        )}

        <div className="catalogTitle">
          <h2>Каталог книг</h2>
          <span>{visibleBooks.length} товаров</span>
        </div>

        {loading ? <Skeleton text="Загружаю каталог..." /> : (
          <div className="bookRail">
            {visibleBooks.map((book) => (
              <BookCard
                key={book.isbn}
                book={book}
                onOpen={() => openBook(book.isbn)}
                onAddToBasket={() => addToBasket(book)}
                onOrderNow={() => orderNow(book)}
                disabledActions={!isAuthorized}
              />
            ))}
          </div>
        )}

        {!loading && visibleBooks.length === 0 && <EmptyState text="Книг по выбранным условиям пока нет" />}
      </div>

      {selectedBook && (
        <Modal onClose={() => setSelectedBook(null)} title={selectedBook.title}>
          <div className="bookDetails">
            <img src={selectedBook.coverUrl || DEFAULT_COVER} alt="" />
            <div>
              <p><b>ISBN:</b> {selectedBook.isbn}</p>
              <p><b>Автор:</b> {selectedBook.author}</p>
              <p><b>Цена:</b> {money(selectedBook.price)}</p>
              <p><b>Наличие:</b> <span className={selectedBook.available ? "status good" : "status bad"}>{selectedBook.available ? "В наличии" : "Нет в наличии"}</span></p>
              <div className="modalActions">
                <button className="secondaryBtn" onClick={() => addToBasket(selectedBook)}>Добавить в корзину</button>
                <button className="primaryBtn" disabled={!selectedBook.available} onClick={() => orderNow(selectedBook)}>Купить сейчас</button>
              </div>
            </div>
          </div>
        </Modal>
      )}
    </section>
  );
}

function FilterPanel({ filters, setFilters, onApply }) {
  return (
    <div className="filterStack">
      <section className="filterCard">
        <h3>Фильтры</h3>
        <label className="check"><input type="checkbox" checked={filters.availableOnly} onChange={(e) => setFilters({ ...filters, availableOnly: e.target.checked })} /> В наличии</label>
        <Select label="Тип" value={filters.type} onChange={(type) => setFilters({ ...filters, type })} options={filterDictionaries.type} />
        <Select label="Сортировка" value={filters.field} onChange={(field) => setFilters({ ...filters, field })} options={filterDictionaries.bookField} />
        <Select label="Порядок" value={filters.direction} onChange={(direction) => setFilters({ ...filters, direction })} options={filterDictionaries.direction} />
        <button className="primaryBtn full" onClick={onApply}>Применить</button>
      </section>
    </div>
  );
}
function BookCard({ book, onOpen, onAddToBasket, onOrderNow, disabledActions }) {
  return (
    <article className="bookCard">
      <button className="coverBtn" onClick={onOpen}>
        <img src={book.coverUrl || DEFAULT_COVER} alt={book.title || "Обложка книги"} />
      </button>
      <div className="bookMetaRow">
        <span className="binding">Твёрдая обложка</span>
        <span className="miniBadge">Book</span>
      </div>
      <h3>{book.title}</h3>
      <p>{book.author}</p>
      <div className="rating">★ ★ ★ ★ ☆ <span>16</span></div>
      <div className="priceRow">
        <b>{money(book.price)}</b>
        <span className={book.available ? "status good" : "status bad"}>{book.available ? "В наличии" : "Нет"}</span>
      </div>
      <button className="primaryBtn buy" onClick={onOrderNow} disabled={!book.available || disabledActions}>Купить</button>
      <button className="secondaryBtn buy" onClick={onAddToBasket} disabled={disabledActions}>Добавить в корзину</button>
    </article>
  );
}

function CartPage({ apiFetch, notify, go, authRequired }) {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);

  const loadBasket = useCallback(async () => {
    setLoading(true);
    try {
      // По текущему контроллеру корзина находится в BookController: /books/basket
      // Если на backend из-за двойной аннотации путь отличается, поменять только здесь.
      const data = await apiFetch("/books/basket", { method: "POST" });
      setBooks(data?.books || []);
    } catch (error) {
      if (error.status !== 401) {
        notify(error.message || "Не удалось загрузить корзину", "error");
      }
    } finally {
      setLoading(false);
    }
  }, [apiFetch, notify]);

  useEffect(() => {
    if (!authRequired) {
      loadBasket();
    }
  }, [authRequired, loadBasket]);

  async function removeFromBasket(isbn) {
    try {
      await apiFetch(`/books/basket/${encodeURIComponent(isbn)}/writeOff`, { method: "POST" });
      notify("Книга удалена из корзины", "success");
      loadBasket();
    } catch (error) {
      notify(error.message || "Не удалось удалить книгу", "error");
    }
  }

  async function checkoutBasket() {
    try {
      const order = await apiFetch("/orders", {
        method: "POST",
      });
      notify("Заказ по корзине создан", "success");
      go("payment", { orderId: order?.orderId, paymentId: order?.paymentId, source: "basket" });
    } catch (error) {
      notify(error.message || "Не удалось оформить корзину", "error");
    }
  }

  if (authRequired) {
    return <EmptyState text="Войдите или зарегистрируйтесь, чтобы открыть профиль" />;
  }

  return (
    <section className="page">
      <PageTitle title="Корзина" subtitle="Книги в корзине хранятся на backend и загружаются через BookController" />
      {loading ? <Skeleton text="Загружаю корзину..." /> : !books.length ? <EmptyState text="Корзина пустая" /> : (
        <>
          <div className="cartList">
            {books.map((book) => (
              <div className="cartItem" key={book.isbn}>
                <img src={book.coverUrl || DEFAULT_COVER} alt="" />
                <div>
                  <h3>{book.title}</h3>
                  <p>{book.author}</p>
                  <b>{money(book.price)}</b>
                </div>
                <button className="linkDangerBtn" onClick={() => removeFromBasket(book.isbn)}>Удалить из корзины</button>
              </div>
            ))}
          </div>
          <div className="summary">
            <b>Итого: {money(books.reduce((sum, book) => sum + Number(book.price || 0), 0))}</b>
            <button className="primaryBtn" onClick={checkoutBasket}>Оформить и перейти к оплате</button>
          </div>
        </>
      )}
    </section>
  );
}

function OrdersPage({ apiFetch, authRequired, notify, go }) {
  const [orders, setOrders] = useState([]);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [filters, setFilters] = useState(orderFilterDefaults);
  const [loading, setLoading] = useState(false);

  const loadOrders = useCallback(async () => {
    setLoading(true);
    try {
      const data = await apiFetch(`/orders${toQuery(cleanOrderFilters(filters))}`);
      setOrders(data?.orders || []);
    } catch (error) {
      if (error.status !== 401) notify(error.message || "Не удалось загрузить заказы", "error");
    } finally {
      setLoading(false);
    }
  }, [apiFetch, filters, notify]);

  useEffect(() => {
    if (!authRequired) loadOrders();
  }, [authRequired, loadOrders]);

  async function openOrder(orderId) {
    try {
      setSelectedOrder(await apiFetch(`/orders/${orderId}`));
    } catch (error) {
      notify(error.message || "Не удалось открыть заказ", "error");
    }
  }

  async function cancelOrder(orderId) {
    try {
      await apiFetch(`/orders/${orderId}/cancel`, { method: "POST" });
      notify("Заказ отменён", "success");
      loadOrders();
    } catch (error) {
      notify(error.message || "Не удалось отменить заказ", "error");
    }
  }

  if (authRequired) return <EmptyState text="Войдите или зарегистрируйтесь, чтобы открыть профиль" />;

  return (
    <section className="page">
      <PageTitle title="Мои заказы" subtitle="История заказов пользователя и переход к оплате" />
      <div className="catalogToolbar">
        <Select label="Тип" value={filters.type} onChange={(type) => setFilters({ ...filters, type })} options={filterDictionaries.orderType} />
        <Select label="Сортировка" value={filters.field} onChange={(field) => setFilters({ ...filters, field })} options={filterDictionaries.orderField} />
        <Select label="Порядок" value={filters.direction} onChange={(direction) => setFilters({ ...filters, direction })} options={filterDictionaries.direction} />
        <label className="check"><input type="checkbox" checked={filters.filtered} onChange={(e) => setFilters({ ...filters, filtered: e.target.checked })} /> Фильтр по периоду</label>
        <input type="datetime-local" value={filters.from} onChange={(e) => setFilters({ ...filters, from: e.target.value })} />
        <input type="datetime-local" value={filters.to} onChange={(e) => setFilters({ ...filters, to: e.target.value })} />
        <button className="secondaryBtn" onClick={loadOrders}>Обновить</button>
      </div>
      {loading ? <Skeleton text="Загружаю заказы..." /> : <OrdersTable orders={orders} onOpen={openOrder} onCancel={cancelOrder} onPay={(order) => go("payment", { orderId: order.id })} />}
      {selectedOrder && <OrderModal order={selectedOrder} onClose={() => setSelectedOrder(null)} />}
    </section>
  );
}

function OrdersTable({ orders, onOpen, onCancel, adminMode = false, onComplete, onPay }) {
  if (!orders.length) return <EmptyState text="Заказов пока нет" />;

  return (
    <div className="tableWrap">
      <table>
        <thead><tr><th>ID</th><th>Статус</th><th>Клиент</th><th>Сумма</th><th>ISBN</th><th>Дата завершения</th><th></th></tr></thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.id}>
              <td>{order.id}</td>
              <td><span className="pill">{order.status}</span></td>
              <td>{order.customerId || "—"}</td>
              <td>{money(order.totalPrice)}</td>
              <td>{(order.isbns || []).join(", ") || "—"}</td>
              <td>{formatDate(order.completionDate)}</td>
              <td className="rowActions">
                <button className="linkBtn" onClick={() => onOpen(order.id)}>открыть</button>
                {!adminMode && <button className="linkBtn" onClick={() => onPay(order)}>оплатить</button>}
                {!adminMode && <button className="linkDangerBtn" onClick={() => onCancel(order.id)}>отменить</button>}
                {adminMode && <button className="linkBtn" onClick={() => onComplete(order.id)}>завершить</button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PaymentPage({ apiFetch, notify, initial, go }) {
  const [paymentId, setPaymentId] = useState(initial?.paymentId || "");
  const [orderId] = useState(initial?.orderId || "");
  const [cards, setCards] = useState([]);
  const [selectedCardId, setSelectedCardId] = useState("");
  const [newCard, setNewCard] = useState({ cardNumber: "", cardHolder: "", expiryMonth: "", expiryYear: "", cvc: "" });
  const [paymentError, setPaymentError] = useState(null);
  const [loading, setLoading] = useState(false);

  const loadCards = useCallback(async () => {
    try {
      const data = await apiFetch("/payments/card/bind");
      const nextCards = Array.isArray(data) ? data : [];
      setCards(nextCards);
      setSelectedCardId((prev) => prev || nextCards[0]?.cardId || "");
    } catch (error) {
      if (error.status !== 401 && error.status !== 403) notify(error.message || "Не удалось загрузить карты", "error");
    }
  }, [apiFetch, notify]);

  useEffect(() => {
    loadCards();
  }, [loadCards]);

  async function bindCard(event) {
    event.preventDefault();
    try {
      const card = await apiFetch("/payments/card/bind", { method: "POST", body: JSON.stringify(newCard) });
      notify("Карта привязана", "success");
      setNewCard({ cardNumber: "", cardHolder: "", expiryMonth: "", expiryYear: "", cvc: "" });
      await loadCards();
      setSelectedCardId(card?.cardId || "");
    } catch (error) {
      notify(error.message || "Не удалось привязать карту", "error");
    }
  }

  async function setDefaultCard(cardId) {
    try {
      await apiFetch("/payments/card/bind", {
        method: "PATCH",
        body: JSON.stringify({ cardId: Number(cardId) }),
      });
      notify("Карта выбрана по умолчанию", "success");
      await loadCards();
      setSelectedCardId(String(cardId));
    } catch (error) {
      notify(error.message || "Не удалось выбрать карту по умолчанию", "error");
    }
  }

  async function startPayment() {
    if (!paymentId) {
      notify("Не передан paymentId. После создания заказа backend должен вернуть paymentId", "warning");
      return;
    }

    setLoading(true);
    setPaymentError(null);
    try {
      const data = await apiFetch(`/payments/${paymentId}/start`, {
        method: "POST",
        body: JSON.stringify(selectedCardId ? { cardId: Number(selectedCardId) } : {}),
      });
      notify(`Платёж: ${data?.status || "запущен"}`, "success");
      go("orders");
    } catch (error) {
      if (isPaymentException(error)) {
        setPaymentError({ title: "Оплата пока недоступна", message: error.message, code: error.code });
        notify("Оплата пока недоступна", "warning");
      } else {
        notify(error.message || "Не удалось начать оплату", "error");
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="page paymentPage">
      <PageTitle title="Оплата заказа" subtitle="Отдельная страница оплаты вызывается после создания заказа из карточки книги, каталога или корзины" />

      <div className="paymentGrid">
        <div className="cardForm">
          <h3>Платёж</h3>
          <label>Order ID<input value={orderId || ""} readOnly placeholder="orderId" /></label>
          <label>Payment ID<input value={paymentId} onChange={(e) => setPaymentId(e.target.value)} placeholder="paymentId из CreateOrderOutput" /></label>
          <label>Привязанная карта
            <select value={selectedCardId} onChange={(e) => setSelectedCardId(e.target.value)}>
              <option value="">Карта по умолчанию</option>
              {cards.map((card) => <option key={card.cardId} value={card.cardId}>{card.maskedPan} · {card.paymentSystem}</option>)}
            </select>
          </label>
          {paymentError && <div className="paymentError"><b>{paymentError.title}</b><p>{paymentError.message}</p><small>{paymentError.code || "PAYMENT_ERROR"}</small></div>}
          <button className="primaryBtn" onClick={startPayment} disabled={loading}>{loading ? "Оплата..." : "Оплатить"}</button>
        </div>

        <form className="cardForm" onSubmit={bindCard}>
          <h3>Привязать реквизиты оплаты</h3>
          <input placeholder="Номер карты" value={newCard.cardNumber} onChange={(e) => setNewCard({ ...newCard, cardNumber: e.target.value })} required />
          <input placeholder="Держатель" value={newCard.cardHolder} onChange={(e) => setNewCard({ ...newCard, cardHolder: e.target.value })} required />
          <div className="twoCols">
            <input placeholder="MM" value={newCard.expiryMonth} onChange={(e) => setNewCard({ ...newCard, expiryMonth: e.target.value })} required />
            <input placeholder="YYYY" value={newCard.expiryYear} onChange={(e) => setNewCard({ ...newCard, expiryYear: e.target.value })} required />
          </div>
          <input placeholder="CVC" value={newCard.cvc} onChange={(e) => setNewCard({ ...newCard, cvc: e.target.value })} required />
          <button className="secondaryBtn">Привязать карту</button>
        </form>
      </div>

      <section className="noteBlock">
        <b>Как ловлю PaymentException на фронте</b>
        <p>apiFetch читает errorCode из ErrorResponse. Если Handler вернёт, например, PAYMENT_UNAVAILABLE или PAYMENT_ERROR, функция isPaymentException покажет отдельный блок «Оплата пока недоступна». Дополнительно есть fallback по тексту ошибки, если errorCode ещё не добавлен</p>
      </section>
    </section>
  );
}

function ProfilePage({ apiFetch, notify, authRequired, user, onLogout }) {
  const [cards, setCards] = useState([]);
  const [newCard, setNewCard] = useState({ cardNumber: "", cardHolder: "", expiryMonth: "", expiryYear: "", cvc: "" });
  const [paymentServiceAvailable, setPaymentServiceAvailable] = useState(true);

  const loadCards = useCallback(async () => {
    try {
      const data = await apiFetch("/payments/card/bind");
      setCards(Array.isArray(data) ? data : []);
      setPaymentServiceAvailable(true);
    } catch (error) {
      if (error.status !== 401 && error.status !== 403) {
        setPaymentServiceAvailable(false);
        notify(error.message || "Сервис оплаты временно недоступен", "warning");
      }
    }
  }, [apiFetch, notify]);

  useEffect(() => {
    if (!authRequired) loadCards();
  }, [authRequired, loadCards]);

  async function bindCard(event) {
    event.preventDefault();
    try {
      const card = await apiFetch("/payments/card/bind", { method: "POST", body: JSON.stringify(newCard) });
      notify("Карта привязана", "success");
      setNewCard({ cardNumber: "", cardHolder: "", expiryMonth: "", expiryYear: "", cvc: "" });
      await loadCards();
    } catch (error) {
      setPaymentServiceAvailable(false);
      notify(error.message || "Не удалось привязать карту", "error");
    }
  }

  async function setDefaultCard(cardId) {
    try {
      await apiFetch("/payments/card/bind", {
        method: "PATCH",
        body: JSON.stringify({ cardId: Number(cardId) }),
      });
      notify("Карта выбрана по умолчанию", "success");
      await loadCards();
    } catch (error) {
      notify(error.message || "Не удалось выбрать карту по умолчанию", "error");
    }
  }

  if (authRequired) return <EmptyState text="Войдите или зарегистрируйтесь, чтобы открыть профиль" />;

  return (
    <section className="page">
      <PageTitle title="Профиль" subtitle="Личные данные и привязанные карты для оплаты заказов" />
      <div className="profileHeader">
        <div>
          <b>{user?.email || "Пользователь"}</b>
          <span>{(user?.roles || []).join(", ") || "ROLE_USER"}</span>
        </div>
        <button className="secondaryBtn" onClick={onLogout}>Выйти</button>
      </div>
      <div className="paymentGrid">
        <div className="cardForm">
          <h3>Привязанные карты</h3>
          {!paymentServiceAvailable && <p className="mutedText">Сервис оплаты сейчас недоступен. Добавление и выбор карты временно невозможны</p>}
          {paymentServiceAvailable && !cards.length && <p className="mutedText">Карт пока нет</p>}
          {cards.map((card) => (
            <div className="savedCard" key={card.cardId}>
              <div>
                <b>{card.maskedPan || `Карта #${card.cardId}`}</b>
                <span>{card.paymentSystem || "Платёжная карта"}</span>
              </div>
              {card.isDefault || card.defaultCard ? (
                <span className="pill">Основная</span>
              ) : (
                <button className="linkBtn" onClick={() => setDefaultCard(card.cardId)}>Основной</button>
              )}
            </div>
          ))}
        </div>

        <form className="cardForm" onSubmit={bindCard}>
          <h3>Добавить карту</h3>
          <input placeholder="Номер карты" value={newCard.cardNumber} onChange={(e) => setNewCard({ ...newCard, cardNumber: e.target.value })} required />
          <input placeholder="Держатель" value={newCard.cardHolder} onChange={(e) => setNewCard({ ...newCard, cardHolder: e.target.value })} required />
          <div className="twoCols">
            <input placeholder="MM" value={newCard.expiryMonth} onChange={(e) => setNewCard({ ...newCard, expiryMonth: e.target.value })} required />
            <input placeholder="YYYY" value={newCard.expiryYear} onChange={(e) => setNewCard({ ...newCard, expiryYear: e.target.value })} required />
          </div>
          <input placeholder="CVC" value={newCard.cvc} onChange={(e) => setNewCard({ ...newCard, cvc: e.target.value })} required />
          <button className="secondaryBtn" disabled={!paymentServiceAvailable}>Добавить карту</button>
        </form>
      </div>
    </section>
  );
}


function AdminPage({ apiFetch, notify }) {
  const [tab, setTab] = useState("orders");
  const tabs = [["orders", "Заказы"], ["books", "Склад книг"], ["requests", "Заявки"], ["analytics", "Аналитика"], ["users", "Права"]];

  return (
    <section className="page">
      <PageTitle title="Панель администратора" subtitle="Раздел показывается только если роль пользователя ADMIN" />
      <div className="tabs">{tabs.map(([id, label]) => <button key={id} className={tab === id ? "tab active" : "tab"} onClick={() => setTab(id)}>{label}</button>)}</div>
      {tab === "orders" && <AdminOrders apiFetch={apiFetch} notify={notify} />}
      {tab === "books" && <AdminBooks apiFetch={apiFetch} notify={notify} />}
      {tab === "requests" && <AdminRequests apiFetch={apiFetch} notify={notify} />}
      {tab === "analytics" && <AdminAnalytics apiFetch={apiFetch} notify={notify} />}
      {tab === "users" && <AdminUsers apiFetch={apiFetch} notify={notify} />}
    </section>
  );
}

function AdminOrders({ apiFetch, notify }) {
  const [orders, setOrders] = useState([]);
  const [filters, setFilters] = useState(orderFilterDefaults);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [path, setPath] = useState("");

  const loadOrders = useCallback(async () => {
    try {
      const data = await apiFetch(`/admin${toQuery(cleanOrderFilters(filters))}`);
      setOrders(data?.orders || []);
    } catch (error) {
      notify(error.message || "Не удалось загрузить админские заказы", "error");
    }
  }, [apiFetch, filters, notify]);

  useEffect(() => { loadOrders(); }, [loadOrders]);

  async function complete(orderId) {
    try {
      await apiFetch(`/admin/orders/${orderId}/complete`, { method: "POST" });
      notify("Заказ завершён", "success");
      loadOrders();
    } catch (error) { notify(error.message || "Не удалось завершить заказ", "error"); }
  }

  async function csv(action) {
    try {
      await apiFetch(`/admin/orders/${action}${toQuery({ path })}`, { method: action === "import" ? "POST" : "GET" });
      notify(action === "import" ? "Импорт выполнен" : "Экспорт выполнен", "success");
      loadOrders();
    } catch (error) { notify(error.message || "CSV-операция не выполнена", "error"); }
  }

  return <div className="adminBlock">
    <div className="catalogToolbar">
      <Select label="Сортировка" value={filters.field} onChange={(field) => setFilters({ ...filters, field })} options={filterDictionaries.orderField} />
      <Select label="Порядок" value={filters.direction} onChange={(direction) => setFilters({ ...filters, direction })} options={filterDictionaries.direction} />
      <button className="secondaryBtn" onClick={loadOrders}>Обновить</button>
    </div>
    <div className="inlineForm"><input placeholder="Путь к CSV" value={path} onChange={(e) => setPath(e.target.value)} /><button className="secondaryBtn" onClick={() => csv("export")}>Экспорт</button><button className="secondaryBtn" onClick={() => csv("import")}>Импорт</button></div>
    <OrdersTable orders={orders} adminMode onOpen={async (id) => setSelectedOrder(await apiFetch(`/orders/${id}`))} onComplete={complete} />
    {selectedOrder && <OrderModal order={selectedOrder} onClose={() => setSelectedOrder(null)} />}
  </div>;
}

function AdminBooks({ apiFetch, notify }) {
  const [form, setForm] = useState({ isbn: "", title: "", author: "", price: "" });
  const [isbn, setIsbn] = useState("");
  const [path, setPath] = useState("");

  async function createBook(event) {
    event.preventDefault();
    try {
      await apiFetch("/books/stock", { method: "POST", body: JSON.stringify({ ...form, price: Number(form.price) }) });
      notify("Книга создана", "success");
      setForm({ isbn: "", title: "", author: "", price: "" });
    } catch (error) { notify(error.message || "Не удалось создать книгу", "error"); }
  }

  async function stockAction(action) {
    if (!isbn) return notify("Укажите ISBN", "info");
    try {
      await apiFetch(`/books/stock/${encodeURIComponent(isbn)}/${action}`, { method: "POST" });
      notify(action === "add" ? "Книга добавлена на склад" : "Книга списана", "success");
    } catch (error) { notify(error.message || "Операция не выполнена", "error"); }
  }

  async function csv(action) {
    try {
      await apiFetch(`/books/stock/${action}${toQuery({ path })}`, { method: action === "import" ? "POST" : "GET" });
      notify(action === "import" ? "Импорт книг выполнен" : "Экспорт книг выполнен", "success");
    } catch (error) { notify(error.message || "CSV-операция не выполнена", "error"); }
  }

  return <div className="adminGrid">
    <form className="cardForm" onSubmit={createBook}><h3>Создать книгу</h3><input placeholder="ISBN" value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} required /><input placeholder="Название" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required /><input placeholder="Автор" value={form.author} onChange={(e) => setForm({ ...form, author: e.target.value })} required /><input type="number" step="0.01" placeholder="Цена" value={form.price} onChange={(e) => setForm({ ...form, price: e.target.value })} required /><button className="primaryBtn">Создать</button></form>
    <div className="cardForm"><h3>Склад</h3><input placeholder="ISBN" value={isbn} onChange={(e) => setIsbn(e.target.value)} /><button className="secondaryBtn" onClick={() => stockAction("add")}>Добавить на склад</button><button className="secondaryBtn" onClick={() => stockAction("writeoff")}>Списать</button></div>
    <div className="cardForm"><h3>CSV</h3><input placeholder="Путь к файлу" value={path} onChange={(e) => setPath(e.target.value)} /><button className="secondaryBtn" onClick={() => csv("export")}>Экспорт</button><button className="secondaryBtn" onClick={() => csv("import")}>Импорт</button></div>
  </div>;
}

function AdminRequests({ apiFetch, notify }) {
  const [requests, setRequests] = useState([]);
  const [filters, setFilters] = useState({ type: "REQUEST", direction: "ASC", field: "TITLE" });
  const load = useCallback(async () => {
    try { const data = await apiFetch(`/requests${toQuery(filters)}`); setRequests(data?.bookRequests || []); }
    catch (error) { notify(error.message || "Не удалось загрузить заявки", "error"); }
  }, [apiFetch, filters, notify]);
  useEffect(() => { load(); }, [load]);

  return <div><div className="catalogToolbar"><Select label="Сортировка" value={filters.field} onChange={(field) => setFilters({ ...filters, field })} options={filterDictionaries.bookField} /><Select label="Порядок" value={filters.direction} onChange={(direction) => setFilters({ ...filters, direction })} options={filterDictionaries.direction} /><button className="secondaryBtn" onClick={load}>Обновить</button></div><div className="bookRail">{requests.map((request) => <BookCard key={request.isbn} book={request} onOpen={() => {}} onAddToBasket={() => {}} onOrderNow={() => {}} disabledActions />)}</div>{!requests.length && <EmptyState text="Заявок пока нет" />}</div>;
}

function AdminAnalytics({ apiFetch, notify }) {
  const [period, setPeriod] = useState({ from: "", to: "" });
  const [earnings, setEarnings] = useState(null);
  const [count, setCount] = useState(null);
  const query = toQuery({ from: toLocalDateTime(period.from), to: toLocalDateTime(period.to) });
  async function loadEarnings() { try { setEarnings((await apiFetch(`/admin/earnings${query}`))?.totalEarnings); } catch (e) { notify(e.message || "Не удалось получить выручку", "error"); } }
  async function loadCount() { try { setCount((await apiFetch(`/admin/orders/completed/count${query}`))?.count); } catch (e) { notify(e.message || "Не удалось получить количество", "error"); } }
  return <div className="analytics"><div className="catalogToolbar"><input type="datetime-local" value={period.from} onChange={(e) => setPeriod({ ...period, from: e.target.value })} /><input type="datetime-local" value={period.to} onChange={(e) => setPeriod({ ...period, to: e.target.value })} /><button className="secondaryBtn" onClick={loadEarnings}>Выручка</button><button className="secondaryBtn" onClick={loadCount}>Кол-во завершённых</button></div><div className="statGrid"><div className="statCard"><span>Выручка</span><b>{earnings === null ? "—" : money(earnings)}</b></div><div className="statCard"><span>Завершённые заказы</span><b>{count === null ? "—" : count}</b></div></div></div>;
}

function AdminUsers({ apiFetch, notify }) {
  const [id, setId] = useState("");
  async function assignAdmin() { if (!id) return notify("Укажите ID пользователя", "info"); try { await apiFetch(`/admin/assign-admin/${id}`, { method: "POST" }); notify("Пользователь назначен администратором", "success"); setId(""); } catch (e) { notify(e.message || "Не удалось назначить администратора", "error"); } }
  return <div className="cardForm"><h3>Назначить администратора</h3><input type="number" placeholder="ID пользователя" value={id} onChange={(e) => setId(e.target.value)} /><button className="primaryBtn" onClick={assignAdmin}>Назначить</button></div>;
}

function OrderModal({ order, onClose }) {
  return <Modal onClose={onClose} title={`Заказ #${order.id}`}><div className="orderDetails"><p><b>Клиент:</b> {order.customerId || "—"}</p><p><b>Статус:</b> {order.status}</p><p><b>Создан:</b> {formatDate(order.createdDate)}</p><p><b>Завершён:</b> {formatDate(order.completionDate)}</p><p><b>Сумма:</b> {money(order.totalPrice)}</p><p><b>ISBN:</b> {(order.isbns || []).join(", ") || "—"}</p></div></Modal>;
}

function PageTitle({ title, subtitle }) { return <div className="pageTitle"><h1>{title}</h1><p>{subtitle}</p></div>; }
function Select({ label, value, onChange, options }) { return <label className="selectLabel"><span>{label}</span><select value={value} onChange={(e) => onChange(e.target.value)}>{options.map((o) => <option value={o} key={o}>{optionLabels[o] || o}</option>)}</select></label>; }
function FilterChip({ label, value }) { return <div className="filterChip"><span>{label}:</span><b>{value}</b></div>; }
function Modal({ title, children, onClose }) { return <div className="modalBackdrop" onMouseDown={onClose}><section className="modal" onMouseDown={(e) => e.stopPropagation()}><div className="modalHeader"><h2>{title}</h2><button className="ghostBtn" onClick={onClose}>✕</button></div>{children}</section></div>; }
function Toast({ toast }) { return <div className={`toast ${toast.variant}`}>{toast.message}</div>; }
function Skeleton({ text }) { return <div className="skeleton">{text}</div>; }
function EmptyState({ text }) { return <div className="empty">{text}</div>; }

class ApiError extends Error { constructor(status, message, code, payload) { super(message); this.status = status; this.code = code; this.payload = payload; } }

async function refreshAccessToken(saveAccessToken) {
  try {
    const response = await fetch(`${API_URL}/auth/refresh`, { method: "POST", credentials: "include" });
    const text = await response.text();
    const data = text ? safeJson(text) : null;
    if (!response.ok || !data?.accessToken) { saveAccessToken(""); return false; }
    saveAccessToken(data.accessToken, buildUserFromAuthResponse(data));
    return true;
  } catch { saveAccessToken(""); return false; }
}

function safeJson(text) { try { return JSON.parse(text); } catch { return { message: text }; } }
function getStoredUser() { try { return JSON.parse(localStorage.getItem(AUTH_USER_KEY) || "null"); } catch { return null; } }
function toQuery(params) { const search = new URLSearchParams(); Object.entries(params || {}).forEach(([k, v]) => { if (k === "availableOnly") return; if (v !== undefined && v !== null && v !== "") search.set(k, String(v)); }); const q = search.toString(); return q ? `?${q}` : ""; }
function cleanOrderFilters(filters) { return { type: filters.type || "ORDER", filtered: filters.filtered, direction: filters.direction || "ASC", field: filters.field || "PRICE", from: filters.filtered ? toLocalDateTime(filters.from) : "", to: filters.filtered ? toLocalDateTime(filters.to) : "" }; }
function cleanBookFilters(filters) { return { type: filters.type || "BOOK", direction: filters.direction || "ASC", field: filters.field || "TITLE" }; }
function matchesBookSearch(book, query) {
  const words = normalizeSearch(query).split(" ").filter(Boolean);
  if (!words.length) return true;
  const haystack = normalizeSearch(`${book?.title || ""} ${book?.author || ""}`);
  return words.every((word) => haystack.includes(word));
}
function normalizeSearch(value) { return String(value || "").toLowerCase().replace(/ё/g, "е").trim(); }
function toLocalDateTime(value) { return value || ""; }
function money(value) { if (value === null || value === undefined || value === "") return "—"; const n = Number(value); if (Number.isNaN(n)) return String(value); return new Intl.NumberFormat("ru-RU", { style: "currency", currency: "RUB", maximumFractionDigits: 2 }).format(n); }
function formatDate(value) { if (!value) return "—"; const date = new Date(value); if (Number.isNaN(date.getTime())) return value; return new Intl.DateTimeFormat("ru-RU", { dateStyle: "short", timeStyle: "short" }).format(date); }
function isPaymentException(error) { const code = String(error?.code || "").toUpperCase(); const msg = String(error?.message || "").toLowerCase(); return code.includes("PAYMENT") || code.includes("BANK") || msg.includes("оплат") || msg.includes("payment") || msg.includes("bank"); }
function buildUserFromAuthResponse(data) {
  const tokenUser = getUserFromToken(data?.accessToken || "");
  const role = data?.role || data?.user?.role;
  const roles = role ? [role] : data?.roles || data?.user?.roles || tokenUser?.roles || [];
  return { ...tokenUser, ...data?.user, email: data?.email || data?.user?.email || tokenUser?.email, roles };
}
function hasRole(user, role) { const roles = user?.roles || []; return roles.some((r) => String(r).replace("ROLE_", "") === role); }
function getUserFromToken(token) {
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
    const rawRoles = payload.authorities || payload.roles || payload.scope || payload.role || [];
    const roles = Array.isArray(rawRoles) ? rawRoles : String(rawRoles).split(/[ ,]+/).filter(Boolean);
    return { id: payload.userId || payload.id || payload.sub, email: payload.email || payload.sub, roles };
  } catch { return null; }
}

const styles = `
:root {
  --canvas: #ffffff;
  --canvas-subtle: #ffffff;
  --surface: #ffffff;
  --surface-muted: #faf6f1;
  --surface-accent: #f1e4d4;
  --ink: #30231d;
  --muted: #75685f;
  --subtle: #9a8777;
  --line: #ece3d8;
  --line-soft: #f4ece3;
  --brand: #7a5a40;
  --brand-hover: #4e3829;
  --brand-soft: #f3e7d9;
  --success: #536b59;
  --success-soft: #edf4ee;
  --danger: #a45a50;
  --danger-soft: #f8ece9;
  --attention: #8a5b26;
  --attention-soft: #fff6e6;
  --shadow: 0 16px 36px rgba(72, 48, 31, 0.08);
  --shadow-soft: 0 1px 0 rgba(72, 48, 31, 0.05);
  --radius: 8px;
  --radius-lg: 12px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
}
* { box-sizing: border-box; }
html { scroll-behavior: smooth; }
body {
  margin: 0;
  min-width: 320px;
  color: var(--ink);
  background: #ffffff;
}
button, input, select { font: inherit; }
button { cursor: pointer; }
button:disabled { cursor: not-allowed; }
.app {
  min-height: 100vh;
  background: #ffffff;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: minmax(270px, 340px) minmax(280px, 1fr) auto auto auto;
  align-items: center;
  gap: 12px;
  min-height: 96px;
  padding: 12px 32px;
  background: rgba(255, 255, 255, .96);
  border-bottom: 1px solid var(--line);
  backdrop-filter: blur(12px);
}
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
  padding: 5px;
  border: 1px solid transparent;
  background: transparent;
  color: var(--ink);
  text-align: left;
}
.brand:hover { background: rgba(255, 248, 237, .7); border-color: var(--line); }
.brandPhoto {
  width: 82px;
  height: 82px;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: 18px;
  box-shadow: 0 10px 26px rgba(72, 48, 31, 0.10);
}
.brandText {
  font-family: Georgia, "Times New Roman", serif;
  font-size: 28px;
  line-height: .95;
  font-weight: 700;
  letter-spacing: -.02em;
}
.searchBox {
  display: flex;
  align-items: center;
  min-width: 240px;
  min-height: 42px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--surface);
  box-shadow: inset 0 1px 0 rgba(208, 215, 222, .35);
}
.searchBox:focus-within {
  border-color: var(--brand);
  box-shadow: 0 0 0 3px rgba(107, 78, 55, .16);
}
.searchBox input {
  flex: 1;
  min-height: 40px;
  border: 0;
  outline: 0;
  background: transparent;
  padding: 0 12px;
  color: var(--ink);
}
.searchBox button {
  width: 42px;
  min-height: 40px;
  border: 0;
  border-left: 0px solid var(--line);
  border-radius: 0 var(--radius) var(--radius) 0;
  background: var(--surface-muted);
  color: var(--brand);
  font-size: 18px;

  display: flex;
  align-items: center;
  justify-content: center;
}
.searchBox button:hover { color: var(--brand-hover); background: var(--brand-soft); }
.nav { display: flex; gap: 4px; justify-content: flex-end; }
.navItem, .loginBtn, .ghostBtn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 38px;
  padding: 7px 12px;
  border: 1px solid transparent;
  border-radius: var(--radius);
  background: transparent;
  color: var(--muted);
  font-size: 14px;
  font-weight: 600;
}
.navIcon { flex: 0 0 auto; width: 20px; height: 20px; }
.navItem .navIcon { opacity: .9; }
.navItem[class*=active] .navIcon { opacity: 1; }
.navItem:hover, .ghostBtn:hover {
  color: var(--ink);
  background: var(--surface-muted);
}
.navItem.active {
  color: var(--brand-hover);
  background: var(--brand-soft);
}
.loginBtn {
  color: var(--surface);
  background: var(--brand);
  border-color: var(--brand);
}
.loginBtn:hover { background: var(--brand-hover); color: var(--surface); }

.shell {
  width: min(1280px, calc(100% - 32px));
  margin: 0 auto;
  padding: 24px 0 48px;
}
.catalogLayout {
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr);
  gap: 24px;
  align-items: start;
}
.leftFilters { position: sticky; top: 96px; }
.filterStack { display: grid; gap: 12px; }
.filterCard, .contentPanel, .authPanel, .cardForm, .noteBlock, .tableWrap, .cartItem, .modal, .statCard, .empty, .skeleton, .profileHeader {
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-soft);
}
.filterCard {
  display: grid;
  gap: 12px;
  padding: 16px;
}
.filterCard::before {
  content: "Old book shelf";
  width: fit-content;
  margin-bottom: 2px;
  padding: 2px 7px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: var(--attention-soft);
  color: var(--attention);
  font-size: 11px;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: .04em;
}
.filterCard h3 {
  margin: 0 0 4px;
  font-family: Georgia, "Times New Roman", serif;
  font-size: 22px;
  line-height: 1.15;
}
.filterCard span { color: var(--muted); }
.contentPanel {
  overflow: hidden;
  padding: 0;
  background: transparent;
  border: 0;
  box-shadow: none;
}
.catalogHero {
  position: relative;
  overflow: hidden;
  padding: 48px clamp(24px, 5vw, 64px);
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  background:
    radial-gradient(circle at 82% 18%, rgba(241, 228, 212, .78), transparent 28%),
    linear-gradient(135deg, #ffffff 0%, #faf6f1 62%, #f1e4d4 100%);
  box-shadow: var(--shadow-soft);
}
.catalogHero::after {
  content: "";
  position: absolute;
  right: clamp(20px, 6vw, 86px);
  bottom: -72px;
  width: 210px;
  height: 210px;
  border-radius: 50%;
  border: 42px solid rgba(122, 90, 64, .08);
}
.eyebrow {
  width: fit-content;
  margin-bottom: 14px;
  padding: 5px 8px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: var(--surface);
  color: var(--muted);
  font-size: 12px;
  font-weight: 700;
}
.catalogHero h1 {
  max-width: 760px;
  margin: 0;
  font-size: clamp(40px, 6vw, 72px);
  line-height: .95;
  letter-spacing: -.055em;
}
.catalogHero p {
  max-width: 680px;
  margin: 18px 0 0;
  color: var(--muted);
  font-size: 18px;
  line-height: 1.5;
}
.heroStats {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 22px;
}
.heroStats span {
  padding: 6px 10px;
  border: 1px solid var(--line);
  border-radius: 999px;
  background: rgba(255, 255, 255, .86);
  color: var(--ink);
  font-size: 13px;
  font-weight: 600;
}
.catalogTitle {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 14px;
  margin: 28px 0 14px;
  padding: 0 2px;
}
.catalogTitle h2, .pageTitle h1 {
  margin: 0;
  font-size: clamp(28px, 4vw, 40px);
  line-height: 1.05;
  letter-spacing: -.035em;
}
.catalogTitle span, .pageTitle p, .mutedText {
  color: var(--muted);
  line-height: 1.5;
}
.page { display: grid; gap: 18px; }
.pageTitle { padding: 2px 0 4px; }
.pageTitle p { max-width: 820px; margin: 8px 0 0; }
.bookRail {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}
.bookCard {
  position: relative;
  display: grid;
  grid-template-columns: 86px 1fr;
  gap: 12px;
  min-height: 174px;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  background: var(--surface);
  box-shadow: var(--shadow-soft);
  transition: border-color .16s ease, box-shadow .16s ease, transform .16s ease;
}
.bookCard:hover {
  transform: translateY(-2px);
  border-color: var(--line-strong, #dbc8b6);
  box-shadow: var(--shadow);
}
.coverBtn {
  grid-row: 1 / span 6;
  width: 86px;
  border: 0;
  background: transparent;
  padding: 0;
}
.coverBtn img {
  display: block;
  width: 86px;
  aspect-ratio: 3 / 4.1;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  box-shadow: 0 6px 16px rgba(27,31,36,.10);
}
.bookMetaRow, .priceRow, .savedCard, .profileHeader, .summary, .modalActions, .tabs, .inlineForm, .authForm {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.bookMetaRow { justify-content: space-between; }
.binding, .miniBadge {
  color: var(--muted);
  font-size: 12px;
  font-weight: 600;
}
.miniBadge, .pill, .status {
  width: fit-content;
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 3px 7px;
  font-size: 12px;
  font-weight: 600;
}
.miniBadge, .pill {
  background: var(--surface-muted);
  color: var(--muted);
  border: 1px solid var(--line);
}
.bookCard h3 {
  margin: 0;
  font-size: 17px;
  line-height: 1.25;
  letter-spacing: -.015em;
}
.bookCard p { margin: 0; color: var(--muted); font-size: 14px; }
.rating { color: var(--attention); font-size: 13px; font-weight: 600; }
.rating span { color: var(--subtle); }
.priceRow { justify-content: space-between; }
.priceRow b { font-size: 18px; color: var(--ink); }
.primaryBtn, .secondaryBtn {
  min-height: 38px;
  padding: 7px 13px;
  border-radius: var(--radius);
  border: 1px solid var(--line);
  font-weight: 600;
  transition: background .16s ease, border-color .16s ease, color .16s ease, box-shadow .16s ease;
}
.primaryBtn {
  color: #ffffff;
  background: var(--brand);
  border-color: var(--brand);
}
.primaryBtn:hover:not(:disabled) { background: rgba(107, 78, 55); }
.secondaryBtn {
  background: var(--surface-muted);
  color: var(--ink);
}
.secondaryBtn:hover:not(:disabled) {
  background: #eaeef2;
  border-color: #afb8c1;
}
.primaryBtn:disabled, .secondaryBtn:disabled {
  opacity: .55;
  box-shadow: none;
}
.buy, .full { width: 100%; }
.bookCard .buy {
  grid-column: 2;
  justify-content: center;
}
.selectLabel {
  display: grid;
  gap: 6px;
  color: var(--muted);
  font-size: 12px;
  font-weight: 600;
}
input, select {
  min-height: 38px;
  width: 100%;
  padding: 7px 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  outline: 0;
  background: var(--surface);
  color: var(--ink);
}
input:focus, select:focus {
  border-color: var(--brand);
  box-shadow: 0 0 0 3px rgba(107, 78, 55, .16);
}
.check {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 40px;
  padding: 8px 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--surface-muted);
  color: var(--ink);
  font-weight: 600;
}
.check input { width: 16px; min-height: 16px; accent-color: var(--brand); }
.authPanel {
  max-width: 980px;
  margin: 0 auto;
  padding: 24px;
  display: grid;
  grid-template-columns: 1fr minmax(280px, 420px);
  gap: 18px;
  align-items: center;
}
.authPanel h2 { margin: 0; font-size: 40px; letter-spacing: -.04em; }
.authPanel p { margin: 10px 0 0; color: var(--muted); }
.authForm { justify-content: flex-end; }
.linkBtn, .linkDangerBtn {
  border: 0;
  background: transparent;
  color: var(--brand);
  font-weight: 600;
  padding: 5px;
}
.linkBtn:hover { text-decoration: underline; }
.linkDangerBtn { color: var(--danger); }
.linkDangerBtn:hover { text-decoration: underline; }
.good {
  background: var(--success-soft);
  color: var(--success);
  border: 1px solid rgba(26, 127, 55, .22);
}
.bad {
  background: var(--danger-soft);
  color: var(--danger);
  border: 1px solid rgba(207, 34, 46, .2);
}
.catalogToolbar {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: end;
  padding: 12px;
  margin-bottom: 16px;
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  background: var(--surface);
}
.catalogToolbar .selectLabel { min-width: 170px; }
.catalogToolbar input { max-width: 230px; }
.tableWrap { overflow: auto; }
table { width: 100%; min-width: 780px; border-collapse: collapse; }
th, td {
  padding: 12px 14px;
  border-bottom: 1px solid var(--line-soft);
  text-align: left;
}
th {
  color: var(--muted);
  background: var(--surface-muted);
  font-size: 12px;
  font-weight: 700;
}
tr:hover td { background: #f6f8fa; }
.rowActions { display: flex; gap: 6px; flex-wrap: wrap; }
.cartList { display: grid; gap: 12px; }
.cartItem {
  display: grid;
  grid-template-columns: 70px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px;
}
.cartItem img {
  width: 70px;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: var(--radius);
}
.cartItem h3 { margin: 0 0 4px; font-size: 17px; }
.cartItem p { margin: 0 0 8px; color: var(--muted); }
.summary {
  justify-content: space-between;
  padding: 16px;
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  background: var(--surface);
}
.paymentGrid, .adminGrid, .statGrid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}
.adminGrid { grid-template-columns: repeat(3, minmax(0, 1fr)); }
.cardForm {
  display: grid;
  gap: 12px;
  padding: 18px;
}
.cardForm h3 {
  margin: 0 0 4px;
  font-size: 20px;
  letter-spacing: -.02em;
}
.cardForm label { display: grid; gap: 6px; color: var(--muted); font-size: 13px; font-weight: 600; }
.twoCols { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.noteBlock {
  padding: 16px;
  color: var(--muted);
  background: var(--surface-accent);
  border-color: #d4a72c;
}
.noteBlock b { color: var(--ink); }
.noteBlock p { margin: 8px 0 0; }
.profileHeader {
  justify-content: space-between;
  padding: 16px;
}
.profileHeader div { display: grid; gap: 3px; }
.profileHeader span { color: var(--muted); }
.savedCard {
  justify-content: space-between;
  padding: 10px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--surface-muted);
}
.savedCard div { display: grid; gap: 2px; }
.savedCard span { color: var(--muted); font-size: 13px; }
.tabs {
  width: fit-content;
  padding: 4px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--surface);
}
.tab {
  min-height: 36px;
  padding: 7px 12px;
  border: 0;
  border-radius: calc(var(--radius) - 2px);
  background: transparent;
  color: var(--muted);
  font-weight: 600;
}
.tab:hover { color: var(--ink); background: var(--surface-muted); }
.tab.active { color: var(--brand); background: var(--brand-soft); }
.inlineForm {
  padding: 12px;
  margin-bottom: 14px;
  border: 1px solid var(--line);
  border-radius: var(--radius-lg);
  background: var(--surface);
}
.inlineForm input { max-width: 360px; }
.analytics { display: grid; gap: 14px; }
.statGrid { margin-top: 2px; }
.statCard {
  display: grid;
  gap: 6px;
  padding: 20px;
}
.statCard span { color: var(--muted); font-size: 13px; }
.statCard b { font-size: 32px; letter-spacing: -.03em; }
.authPanel, .paymentPage { align-items: start; }
.paymentError {
  padding: 12px;
  border: 1px solid rgba(207, 34, 46, .2);
  border-radius: var(--radius);
  background: var(--danger-soft);
  color: var(--danger);
}
.paymentError p { margin: 6px 0; color: var(--danger); }
.paymentError small { color: var(--muted); }
.bookDetails {
  display: grid;
  grid-template-columns: 180px 1fr;
  gap: 18px;
  align-items: start;
}
.bookDetails img {
  width: 100%;
  aspect-ratio: 3 / 4;
  object-fit: cover;
  border: 1px solid var(--line);
  border-radius: var(--radius);
}
.modalActions { margin-top: 16px; }
.orderDetails p { margin: 8px 0; }
.modalBackdrop {
  position: fixed;
  inset: 0;
  z-index: 50;
  display: grid;
  place-items: center;
  padding: 18px;
  background: rgba(31, 35, 40, .42);
}
.modal {
  width: min(760px, 100%);
  max-height: min(760px, calc(100vh - 36px));
  overflow: auto;
  padding: 18px;
  box-shadow: var(--shadow);
}
.modalHeader {
  display: flex;
  justify-content: space-between;
  gap: 14px;
  align-items: start;
  margin-bottom: 14px;
}
.modalHeader h2 { margin: 0; font-size: 24px; letter-spacing: -.025em; }
.ghostBtn {
  width: 36px;
  min-height: 36px;
  padding: 0;
  color: var(--muted);
}
.toast {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 80;
  max-width: min(440px, calc(100vw - 40px));
  padding: 12px 14px;
  border: 1px solid var(--line);
  border-radius: var(--radius);
  background: var(--surface);
  color: var(--ink);
  box-shadow: var(--shadow);
  font-weight: 600;
}
.toast.success { border-color: rgba(26,127,55,.28); background: var(--success-soft); color: var(--success); }
.toast.error { border-color: rgba(207,34,46,.28); background: var(--danger-soft); color: var(--danger); }
.toast.warning { border-color: #d4a72c; background: var(--attention-soft); color: var(--attention); }
.empty, .skeleton {
  padding: 28px;
  color: var(--muted);
  text-align: center;
}
.skeleton {
  position: relative;
  overflow: hidden;
  background: linear-gradient(90deg, var(--surface) 0%, var(--surface-muted) 45%, var(--surface) 100%);
}
.filterChip { display: none; }

@media (max-width: 1120px) {
  .topbar {
    grid-template-columns: 1fr;
    align-items: stretch;
  }
  .nav { justify-content: flex-start; flex-wrap: wrap; }
  .catalogLayout { grid-template-columns: 1fr; }
  .leftFilters { position: static; }
  .filterCard {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    align-items: end;
  }
  .filterCard::before, .filterCard h3 { grid-column: 1 / -1; }
}
@media (max-width: 760px) {
  .shell { width: min(100% - 20px, 1280px); padding-top: 14px; }
  .topbar { padding: 12px; }
  .catalogHero { padding: 30px 20px; }
  .catalogHero h1 { font-size: 38px; }
  .bookRail { grid-template-columns: 1fr; }
  .bookCard { grid-template-columns: 76px 1fr; }
  .coverBtn, .coverBtn img { width: 76px; }
  .paymentGrid, .adminGrid, .statGrid, .authPanel { grid-template-columns: 1fr; }
  .cartItem { grid-template-columns: 58px 1fr; }
  .cartItem .linkDangerBtn { grid-column: 1 / -1; justify-self: start; }
  .bookDetails { grid-template-columns: 1fr; }
  .filterCard { grid-template-columns: 1fr; }
}
@media (max-width: 480px) {
  .bookCard { grid-template-columns: 1fr; }
  .coverBtn, .coverBtn img { width: 100%; }
  .bookCard .buy { grid-column: 1; }
  .catalogTitle { display: grid; }
}
`

export default App;
