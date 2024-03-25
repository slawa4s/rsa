import random

from rsa import is_prime, get_random_prime, decrypt_rsa, encrypt_rsa, generate_rsa_keys


def test_is_prime():
    assert is_prime(2) == True
    assert is_prime(9) == False
    assert is_prime(13) == True


def test_get_random_prime():
    for _ in range(10):
        assert is_prime(get_random_prime()) == True


def test_rsa_encryption():
    for _ in range(10):
        public_key, private_key = generate_rsa_keys()
        data = random.randint(100000, 999999)
        encrypted = encrypt_rsa(data, public_key)
        decrypted = decrypt_rsa(encrypted, private_key)
        assert data == decrypted


test_is_prime()
test_get_random_prime()
test_rsa_encryption()
