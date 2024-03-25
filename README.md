usage: console_app.py [-h] [-i INPUT] [-o OUTPUT] [-k KEY] {generate_keys,encrypt,decrypt}

RSA-slawa4s

positional arguments:
  {generate_keys,encrypt,decrypt}
                        Command

options:
  -h, --help            show this help message and exit
  -i INPUT, --input INPUT
                        Input file
  -o OUTPUT, --output OUTPUT
                        Output file
  -k KEY, --key KEY     Public or private key

Run example
```bash
echo '123456789' > input.txt
python console_app.py generate_keys
python console_app.py encrypt -i input.txt -o encrypted.txt -k $(cat public_key.pem)
python console_app.py decrypt -i encrypted.txt -o result.txt -k $(cat private_key.pem)
diff input.txt result.txt
```