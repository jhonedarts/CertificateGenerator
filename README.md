# Certificate Generator
Gerador de Certificados para eventos/cursos<br>
criado para a XVII SIECOMP - UEFS

Setup: Esta aplicação usa a biblioteca do iText pra manipular pdfs. Encontra-se na pasta "libs" dentro da pasta do projeto "CertificateGenerator"<br>

Preparando a execução

1- Escreva os dados em um txt e salve na pasta "in" no mesmo diretório em que se encontra o .jar ou na pasta do projeto "CertificateGenerator"<br>
2- Execute<br>
3- Os certificados gerados serão salvos na pasta "results"<br>

Formato do arquivo de entrada:
```
/tipo /nome do curso ;08
/nome do palestrante

nome do participante
nome do participante
...
nome do participante

```

O tipo só pode ser "minicurso", "palestra", "monitores" e "organização". Sendo que estess dois últimos dispensam nome do curso e palestrante.
0 "08" ao lado de "/nome do curso" representa a carga horária, neste exemplo, 08 horas.

Obs:<br>

Pode escrever tudo em minúsculo.<br>
Pode adicionar mais de um curso no mesmo txt.<br>
Pode por varios txt.<br>
O nome do arquivo txt nao importa.<br>