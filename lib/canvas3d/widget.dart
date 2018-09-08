part of canvas3d;

class Canvas3d extends StatelessWidget {
  final DrawParameters parameters;

  Canvas3d({
    Key key,
    @required this.parameters,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) => Container(
        constraints: BoxConstraints.expand(),
        child: CustomPaint(
          painter: _Canvas3dPainter(parameters),
        ),
      );
}
