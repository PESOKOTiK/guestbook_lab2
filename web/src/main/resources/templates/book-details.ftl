<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>${book.title} — Деталі та коментарі</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f9; color: #222; margin: 0; padding: 30px; }
        .container { max-width: 820px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); }
        .back { color: #2563eb; text-decoration: none; font-weight: bold; }
        .book-card { background: #f8fafc; padding: 18px; border-radius: 6px; border: 1px solid #e2e8f0; margin: 16px 0; }
        .comment-item { padding: 12px 0; border-bottom: 1px solid #e2e8f0; }
        form { margin-top: 20px; background: #f8fafc; padding: 16px; border-radius: 6px; border: 1px solid #e2e8f0; }
        label { display: block; margin-top: 10px; font-weight: bold; }
        select, textarea { width: 100%; padding: 8px; margin-top: 4px; border: 1px solid #cbd5e1; border-radius: 5px; box-sizing: border-box; }
        button { margin-top: 14px; padding: 9px 16px; background: #2563eb; color: #fff; border: none; border-radius: 5px; font-weight: bold; cursor: pointer; }
        a.user-link { color: #2563eb; font-weight: bold; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <a class="back" href="/books">&larr; Назад до каталогу</a>

    <div class="book-card">
        <h1 style="margin-top:0;">${book.title}</h1>
        <p><strong>Автор:</strong> ${book.author} | <strong>Рік видання:</strong> ${book.year?c}</p>
        <p>${book.description}</p>
    </div>

    <h2>Коментарі до книги (${comments?size})</h2>
    <#if comments?size == 0>
        <p>Поки що немає жодного коментаря. Будьте першим!</p>
    <#else>
        <#list comments as c>
            <div class="comment-item">
                <p style="margin:0 0 6px 0;">
                    Автор: <a class="user-link" href="/users/${c.user.id}/comments">${c.user.username}</a>
                    <span style="color:#64748b; font-size:13px;">(${c.createdAt})</span>
                </p>
                <p style="margin:0;">${c.text}</p>
            </div>
        </#list>
    </#if>

    <form action="/books/${book.id}/comments" method="post">
        <h3 style="margin-top:0;">Додати новий коментар</h3>
        <label>Користувач:</label>
        <select name="userId" required>
            <#list users as u>
                <option value="${u.id}">${u.username} (${u.role})</option>
            </#list>
        </select>

        <label>Текст коментаря:</label>
        <textarea name="text" rows="3" placeholder="Напишіть ваш відгук про книгу..." required></textarea>

        <button type="submit">Опублікувати коментар</button>
    </form>
</div>
</body>
</html>
