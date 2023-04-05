Help:
```
Usage: RSA-slawa4s options_list
Arguments: 
    command -> Command { Value should be one of [encrypt, decrypt, generate_keys] }
Options: 
    --inputFile, -i -> Input file { String }
    --outputFile, -o -> Output file { String }
    --providedKey, -k -> Public or private key { String }
    --help, -h -> Usage info 
```
Run example
```bash
echo 'Hello World!' > input.txt
./gradlew run --args="generate_keys"
./gradlew run --args="encrypt -i input.txt -o encrypted.txt -k $(cat public_key.pem)"
./gradlew run --args="decrypt -i encrypted.txt -o result.txt -k $(cat private_key.pem)"
diff input.txt result.txt
```