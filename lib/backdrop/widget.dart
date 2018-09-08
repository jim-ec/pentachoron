part of backdrop;

class Backdrop extends StatefulWidget {
  final Widget backLayer;
  final Widget frontLayer;

  const Backdrop({
    Key key,
    @required this.backLayer,
    @required this.frontLayer,
  }) : super(key: key);

  @override
  _BackdropState createState() => _BackdropState(backLayer, frontLayer);
}

