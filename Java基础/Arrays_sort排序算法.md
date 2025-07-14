在JDK中，`Arrays.sort`方法的底层排序算法**依赖于数据类型**：

- **对于基本类型数组（如 int[], double[] 等）：**
  - **JDK 6 及之前**：使用的是“快速排序”（QuickSort）。
  - **JDK 7 及之后**：使用的是“双轴快速排序”（Dual-Pivot Quicksort），这是 Vladimir Yaroslavskiy 提出的改进版快速排序，性能更好。

- **对于对象类型数组（如 Integer[], String[] 等）：**
  - 使用的是**TimSort**，一种结合了归并排序和插入排序的算法，适合部分有序的数据，性能和稳定性都很好。

**简要总结：**
- 基本类型数组：JDK 7+ 用“双轴快速排序”。
- 对象类型数组：用 TimSort（归并+插入混合，稳定排序）。

