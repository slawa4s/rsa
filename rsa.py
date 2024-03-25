import math
import random

MIN_PRIME = 2 ** 31
MAX_PRIME = 2 ** 32


def is_prime(num):
    if num < 2:
        return False
    for i in range(2, math.isqrt(num) + 1):
        if num % i == 0:
            return False
    return True


def get_random_prime():
    num = random.randint(MIN_PRIME, MAX_PRIME)
    while not is_prime(num):
        num = random.randint(MIN_PRIME, MAX_PRIME)
    return num


def gcd(a, b):
    while b != 0:
        a, b = b, a % b
    return a


def generate_coprime_number(number):
    while True:
        result = random.randint(2, number)
        if math.gcd(result, number) == 1:
            return result


def gcd_plus(a, b):
    if b == 0:
        return a, 1, 0
    else:
        gcd, x, y = gcd_plus(b, a % b)
        return gcd, y, x - (a // b) * y


def generate_public_key(private_key, m):
    _, x, _ = gcd_plus(private_key, m)
    return x % m


def generate_rsa_keys():
    p = get_random_prime()
    q = get_random_prime()

    n = p * q
    m = (p - 1) * (q - 1)

    private_key = generate_coprime_number(m)
    return (generate_public_key(private_key, m), n), (private_key, n)


def encrypt_rsa(message, public_key):
    e, n = public_key
    return pow(message, e, n)


def decrypt_rsa(encrypted, private_key):
    d, n = private_key
    return pow(encrypted, d, n)
