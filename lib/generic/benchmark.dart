
class Benchmark {
  
  DateTime t = DateTime.now();

  Benchmark.start();

  void step(final String what) {
    final now = DateTime.now();
    print("${t.difference(now).inMilliseconds}ms: $what");
  }
  
}
