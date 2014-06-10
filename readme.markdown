## NAME

 ad-edit
 
## SYNOPSIS

```
java -jar ad-edit.jar insert-field field [--source n] [--format ext] --exec command [| command ...]
```

```
java -jar ad-edit.jar set-field field [--source n] [--format ext] --exec command [| command ...]
```

```
java -jar ad-edit.jar drop-field field
```

```
java -jar ad-edit.jar help
```

## DESCRIPTION

Edits a TSV file. The input is read from STDIN and the result is output to STDOUT. The options are as follows:

---

### insert-field
Generates new field and inserts it before the specified `field`.

##### field
Target field number.


##### --source n
Source field number. If specified, the value of the field that corresponds to this value will be given to the first `command` in `--exec` clause as STDIN input.


##### --format ext
The format of the data that the last `command` in `--exec` clause will be return. Allowed values are `jpg`, `png`, `tif`, `gif`, `svg`, `wav`, `mp3`, `ogg`, `flac`, `mp4`, `swf`, `mov`, `mpg`, `mkv`, `m4a`, `html` and `txt`.

##### --exec command [| command ...]
Definition of a generator of new field. `command` is a command expression in a shell(e.g. `echo -n hello!`). `--exec` executes `command` and the result will be the data of new field. In the avobe instance, the result will be `hello!`.

`command` supports Template. The template expression is `${ }`. Build-in properties are `field` and `media`. The `field` property provides access to the data of the fields. The result of `... --exec echo -n "${ field(0) }"` is equivalent to the result of `... --source 0 --exec cat`. The `media` property has `dir` property that returns the absolute path to `./collection.media`. To escape template expression, use `$${  }`. A command `... --exec echo  -n "$${ field(0) }"` will be output `$${ field(0) }` as is.

`--exec` supports Pipeline like a shell. The output of a command is connected to the input of the following command. And the output of last command will be treated as the result. The pipeline character is `|`, just the same as in a shell, but note that it in `--exec` clause must be escaped with shell's escape character for that reason. 

---
      
### set-field
Generates new field and overwrites the specified `field`.

Options are the same as `insert-field`.

---

### drop-field
Deletes the specified `field`.

##### field
Target field number.

---

### help
Prints only "See https://github.com/rubyu/ad-edit" :P
