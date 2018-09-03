import 'package:flutter/material.dart';

class Backdrop extends StatefulWidget {
  final Widget backBar;
  final Widget backLayer;
  final Widget frontLayer;

  const Backdrop({
    Key key,
    @required this.backBar,
    @required this.backLayer,
    @required this.frontLayer,
  }) : super(key: key);

  @override
  _BackdropState createState() =>
      _BackdropState(backBar, backLayer, frontLayer);
}

class _BackdropState extends State<Backdrop> {
  /// The height of the back bar, matching the height of the app bar
  /// as per Material design specifications.
  ///
  /// https://material.io/design/components/app-bars-top.html#specs
  static const backBarHeight = 56.0;

  final Widget backBar;
  final Widget backLayer;
  final Widget frontLayer;

  _BackdropState(this.backBar, this.backLayer, this.frontLayer);

  @override
  Widget build(BuildContext context) => LayoutBuilder(
        builder: (context, constraints) {
          final frontPanelTop = constraints.biggest.height - 64.0;
          final backPanelHeight = constraints.biggest.height - backBarHeight;
          return Stack(
            fit: StackFit.expand,
            children: <Widget>[
              Container(
                color: Theme.of(context).primaryColor,
              ),
              Positioned(
                top: 0.0,
                height: backBarHeight,
                left: 0.0,
                right: 0.0,
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: backBar,
                ),
              ),
              Positioned(
                top: backBarHeight,
                height: backPanelHeight,
                left: 0.0,
                right: 0.0,
                child: backLayer,
              ),
              Positioned.fill(
                top: frontPanelTop,
                bottom: -frontPanelTop,
                left: 0.0,
                right: 0.0,
                child: frontLayer,
              ),
            ],
          );
        },
      );
}
