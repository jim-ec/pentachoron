
class Benchmark {
  
  DateTime t = DateTime.now();

  Benchmark.start();

  void step(final String what) {
    final now = DateTime.now();
    print("${now.difference(t).inMilliseconds}ms: $what");
  }
  
}
