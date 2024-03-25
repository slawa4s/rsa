import argparse

from rsa import generate_rsa_keys, encrypt_rsa, decrypt_rsa


def write_to_file(file_path, content):
    with open(file_path, 'w') as f:
        f.write(content)


def read_from_file(file_path):
    with open(file_path, 'r') as f:
        return f.read()


def serialize(key):
    return ','.join(map(str, key))


def main():
    parser = argparse.ArgumentParser(description="RSA-slawa4s")
    parser.add_argument("command", choices=["generate_keys", "encrypt", "decrypt"], help="Command")
    parser.add_argument("-i", "--input", help="Input file")
    parser.add_argument("-o", "--output", help="Output file")
    parser.add_argument("-k", "--key", help="Public or private key")

    args = parser.parse_args()

    if args.command == "generate_keys":
        public_key, private_key = generate_rsa_keys()

        print(
            f"Generated keys:\nPUBLIC KEY (saved to public_key.pem): {public_key} \n"
            f"PRIVATE KEY (saved to private_key.pem): {private_key}")
        write_to_file("public_key.pem", serialize(public_key))
        write_to_file("private_key.pem", serialize(private_key))

    elif args.command == "encrypt":
        if args.input is None or args.output is None or args.key is None:
            raise ValueError(
                "Both input and output files and public key are required for encryption!")

        with open(args.input, 'r') as f:
            message = int(f.read())

        encrypted = encrypt_rsa(message, tuple(map(int, args.key.split(','))))
        write_to_file(args.output, str(encrypted))

    elif args.command == "decrypt":
        if args.input is None or args.output is None or args.key is None:
            raise ValueError(
                "Both input and output files and private key are required for decryption!")

        with open(args.input, 'r') as f:
            encrypted_msg = int(f.read())

        decrypted = decrypt_rsa(encrypted_msg, tuple(map(int, args.key.split(','))))
        write_to_file(args.output, str(decrypted) + '\n')


if __name__ == "__main__":
    main()
