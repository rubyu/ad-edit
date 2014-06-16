## NAME

wok
 
## SYNOPSIS

```
java -jar wok.jar insert `column` `script` [`arg` [`arg` ...]]
```

```
java -jar wok.jar set `column` `script` [`arg` [`arg` ...]]
```

```
java -jar wok.jar drop `column`
```

## DESCRIPTION

Edits a TSV(Tab separated CSV) file.
The input is read from STDIN and the result is output to STDOUT.
The input must be encoding in UTF-8.
The options are as follows:

---

### insert
Generates new column and inserts it before the specified `column`.

##### column
Target column number.

##### script
The script written in Scalate's SSP format.

Build-in variables are `row`, `arg` and `media`.
`row` provides access to the data of columns of a row.
`media` has `dir` property that returns the absolute path to `./collection.media`.
`arg` provides access to given arguments.

Build-in Class is `Proc`.
`Proc` executes commands given in the constructor. `${ Proc("echo", "-n", "hello!").exec() }` will be `hello!`.
`Proc` supports Pipe like a shell.
And the output of last command will be treated as the result.
The pipeline character is `|`, just the same as in a shell, but note that it must be escaped with shell's escape character.

---

##### arg
Arguments will be passed to, as `arg`, in the `script`.

---

### set
Generates new column and overwrites the specified `column` by it.

Options are the same as `insert` command.

---

### drop
Deletes the specified `column`.

##### column
Target column number.
