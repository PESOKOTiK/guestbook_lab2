<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Коментарі користувача: ${username}</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f9; color: #222; margin: 0; padding: 30px; }
        .container { max-width: 820px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); }
        .back { color: #2563eb; text-decoration: none; font-weight: bold; }
        ul { list-style: none; padding: 0; margin-top: 18px; }
        li { background: #f8fafc; padding: 14px 18px; border-radius: 6px; border: 1px solid #e2e8f0; margin-bottom: 10px; }
        hr { border: none; border-top: 1px solid #e2e8f0; margin: 14px 0; }
        a { color: #2563eb; font-weight: bold; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <a class="back" href="/books">&larr; Назад до списку книг</a>
    <h1>Коментарі користувача: ${username}</h1>

    <#if comments?size == 0>
        <p>Цей користувач ще не залишав коментарів.</p>
    <#else>
        <ul>
            <#list comments as c>
                <li>
                    <p><strong>${c.createdAt}</strong></p>
                    <p>${c.text}</p>
                    <p>Книга: <a href="/books/${c.book.id}">Book #${c.book.id} (${c.book.title})</a></p>
                </li>
                <hr>
            </#list>
        </ul>
    </#if>
</div>
</body>
</html>
