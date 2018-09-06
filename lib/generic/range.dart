import 'package:meta/meta.dart';

/// Returns a int range, inclusively beginning [start] and exclusively
/// running to [to]. The step size is determined by [step].
/// If [step] is not `1`, the value `to - 1` is not guaranteed to be reached.
/// No parameter must be `null`.
Iterable<int> range({
  final int start = 0,
  @required final int to,
  final int step = 1,
}) sync* {
  assert(start != null && to != null && step != null, "Nothing must be null");
  assert(to > start, "Range end must be greater than the range start");
  assert(step > 0, "Step must be positive and not null");
  
  for (var i = start; i < to; i += step) {
    yield i;
  }
}

/// Returns a double range, inclusively beginning [start] and exclusively
/// running to [to]. The step size is determined by [step].
/// If [step] is not `1`, the value `to - 1` is not guaranteed to be reached.
/// No parameter must be `null`.
Iterable<double> rangeDoubles({
  final double start = 0.0,
  @required final double to,
  final double step = 1.0,
}) sync* {
  assert(start != null && to != null && step != null, "Nothing must be null");
  assert(to > start, "Range end must be greater than the range start");
  assert(step > 0.0, "Step must be positive and not null");
  
  for (var i = start; i < to; i += step) {
    yield i;
  }
}
