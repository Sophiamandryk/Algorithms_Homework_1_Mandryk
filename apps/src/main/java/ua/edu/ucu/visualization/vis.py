import matplotlib.pyplot as plt
import pandas as pd

data = pd.DataFrame({
    "size": [100, 1000, 10000, 100000],
    "TreeSet+HashMap": [13660079, 9903540, 4891887, 4470791],
    "ArrayList": [14533180, 1621295, 126524, 3070],
    "TreeMap": [100587, 387422, 1117745, 2682521]
})

plt.figure(figsize=(8,5))
plt.plot(data["size"], data["TreeSet+HashMap"], marker="o", label="TreeSet + HashMap")
plt.plot(data["size"], data["ArrayList"], marker="s", label="ArrayList")
plt.plot(data["size"], data["TreeMap"], marker="^", label="TreeMap")

# plt.xscale("log")
# plt.yscale("log")
plt.title("Benchmark: Кількість операцій за 10 секунд (A:B:C = 50:10:5)")
plt.xlabel("N")
plt.ylabel("Кількість операцій ")
plt.grid(True, which="both", linestyle="--", linewidth=0.7)
plt.legend()
plt.tight_layout()

plt.savefig("benchmark_comparison.png", dpi=150)
plt.show()

