<!DOCTYPE html>
<html lang="uk">
<head>
    <meta charset="UTF-8">
    <title>Додати книгу</title>
    <style>
        body { font-family: Arial, sans-serif; background: #f4f6f9; color: #222; margin: 0; padding: 30px; }
        .container { max-width: 540px; margin: 0 auto; background: #fff; padding: 25px; border-radius: 8px; box-shadow: 0 2px 6px rgba(0,0,0,0.08); }
        label { display: block; margin-top: 12px; font-weight: bold; }
        input, textarea { width: 100%; padding: 8px; margin-top: 5px; border: 1px solid #cbd5e1; border-radius: 5px; box-sizing: border-box; }
        button { margin-top: 18px; padding: 10px 16px; background: #2563eb; color: #fff; border: none; border-radius: 5px; font-weight: bold; cursor: pointer; }
        a { margin-left: 12px; color: #64748b; text-decoration: none; }
    </style>
</head>
<body>
<div class="container">
    <h1>Нова книга</h1>
    <form action="/books/add" method="post">
        <label>Назва книги:</label>
        <input type="text" name="title" required />

        <label>Автор:</label>
        <input type="text" name="author" required />

        <label>Рік видання:</label>
        <input type="number" name="year" value="2024" required />

        <label>Опис:</label>
        <textarea name="description" rows="3"></textarea>

        <button type="submit">Зберегти</button>
        <a href="/books">Скасувати</a>
    </form>
</div>
</body>
</html>
