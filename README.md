# 🎲 Lotto Generator

A simple Java console application for generating Romanian lottery tickets.

## Features

- ✅ Loto 6/49
- ✅ Loto 5/40
- ✅ Joker
- ✅ SecureRandom number generation
- ✅ Prevents duplicate generated tickets
- ✅ Calculates ticket costs
- ✅ Uses BigDecimal for monetary calculations

## Technologies

- Java 21
- SecureRandom
- Streams API
- BigDecimal
- LinkedHashSet

## Example Output

```text
Loto 6/49:
[3, 6, 8, 12, 38, 42]
[8, 14, 16, 20, 21, 24]

Cost: 16.50 RON

Joker:
[5, 12, 23, 31, 44] | Joker: 17

Cost: 14.50 RON
```

## Disclaimer

This project generates random lottery tickets using `SecureRandom`.

It does **not** increase the probability of winning or predict future lottery numbers.