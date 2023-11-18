# minicel
A very simple excel engine which can take a file like this:

```csv
A       |B
1       |2
3       |4
=A1+B1  |=A2+B2
```

and output a file like this:
```csv
A       |B
1       |2
3       |4
3       |7
```
