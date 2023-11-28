# minicel
A very simple excel engine which can take a file like this:

```csv
A           |B
12          |= A1 / 2
= B1 / 2    |= ((A2 / 2) * 3) + 2
```

and output a file like this:
```csv
A          |B
12         |2
3          |6.5
```

