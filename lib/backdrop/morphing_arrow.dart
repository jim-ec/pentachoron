part of backdrop;

class MorphingArrow extends StatelessWidget {
  final double advance;
  final Color color;

  const MorphingArrow({
    Key key,
    @required this.advance,
    @required this.color,
  }) : super(key: key);

  @override
  Widget build(final BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _MorphingArrowPainter(advance, color),
        ),
      );
}

@immutable
class _Line {
  final Offset from, to;
  const _Line(this.from, this.to);
}

@immutable
class _LinePoles {
  final _Line start, end;
  const _LinePoles(this.start, this.end);

  _Line lerp(final double advance) => _Line(
    Offset.lerp(start.from, end.from, advance),
    Offset.lerp(start.to, end.to, advance),
  );
}

class _MorphingArrowPainter extends CustomPainter {
  final double advance;
  final Paint arrowPaint;
  
  static const linePoles = [
    _LinePoles(
      _Line(Offset(0.3, 0.4), Offset(0.5, 0.6)),
      _Line(Offset(0.3, 1.0 - 0.4), Offset(0.5, 1.0 - 0.6)),
    ),
    _LinePoles(
      _Line(Offset(0.5, 0.6), Offset(0.7, 0.4)),
      _Line(Offset(0.5, 1.0 - 0.6), Offset(0.7, 1.0 - 0.4)),
    ),
  ];

  _MorphingArrowPainter(this.advance, final Color color)
      : arrowPaint = Paint()
          ..color = color
          ..strokeWidth = 2.0
          ..strokeCap = StrokeCap.round;

  @override
  bool shouldRepaint(final _MorphingArrowPainter oldDelegate) =>
      oldDelegate.advance != advance;

  @override
  void paint(final Canvas canvas, final Size size) => linePoles
          .map((linePoles) => linePoles.lerp(advance))
          .expand((line) => [line.from, line.to])
          .map((point) => point.scale(size.width, size.height))
          .reduce((from, to) {
        canvas.drawLine(from, to, arrowPaint);
        return to;
      });
}
