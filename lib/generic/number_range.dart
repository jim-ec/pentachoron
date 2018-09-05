double remap(
  double x,
  double lowerFrom,
  double upperFrom,
  double lowerTo,
  double upperTo,
) =>
    (x - lowerFrom) / (upperFrom - lowerFrom) * (upperTo - lowerTo) + lowerTo;
