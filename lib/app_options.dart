import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

typedef void OnOptionChanged(VoidCallback fn);

/// Widget providing access to app options.
/// Children widget can access the app options using [AppOptions].
/// Besides wrapping all your widgets which needs to access persistent
/// app options into this widget class, you don't need to mention it at all.
/// Instead, the actual accessing happens through the instance [AppOptions],
/// available through a call to [AppOptions.of].
class AppOptionsProvider extends StatefulWidget {
  final Widget child;

  const AppOptionsProvider({Key key, this.child}) : super(key: key);

  @override
  _AppOptionsState createState() => _AppOptionsState();
}

class _AppOptionsState extends State<AppOptionsProvider> {
  SingleStatefulAppOption _invertedHorizontalCamera;

  _AppOptionsState() {
    _invertedHorizontalCamera =
        SingleStatefulAppOption("inverted_horizontal_camera", setState);
  }

  @override
  Widget build(BuildContext context) => AppOptions(
        invertedHorizontalCamera:
            SingleStatelessAppOption(_invertedHorizontalCamera),
        child: widget.child,
      );
}

/// The actual class listing all the options.
/// Obtaining access works through a call to [AppOptions.of].
class AppOptions extends InheritedWidget {
  final SingleStatelessAppOption invertedHorizontalCamera;

  const AppOptions({
    Key key,
    @required this.invertedHorizontalCamera,
    @required Widget child,
  })  : assert(child != null),
        super(key: key, child: child);

  static AppOptions of(BuildContext context) {
    return context.inheritFromWidgetOfExactType(AppOptions);
  }

  @override
  bool updateShouldNotify(final AppOptions old) =>
      invertedHorizontalCamera._creationValue !=
      old.invertedHorizontalCamera._creationValue;
}

/// Instances of this class keep track of the actual option values,
/// while saving them to persistent storage.
///
/// Instances of this class are *not* directly used to access options,
/// but rather are wrapped by immutable views, saving the option value
/// at construction time.
/// They are recreated every time the actual options changes.
/// This makes comparing to previous option value far easier when rebuilding,
/// because one can compare two values from different time points,
/// which would not be possible if one would use the actual, stateful option
/// value provided by this class.
class SingleStatefulAppOption {
  final String key;
  final OnOptionChanged onChanged;

  bool _value = false;

  set value(value) {
    onChanged(() {
      _value = value;
    });
    SharedPreferences.getInstance().then((final prefs) {
      value = prefs.setBool(key, value);
    });
  }

  get value => _value;

  /// Create a new option.
  ///
  /// [name] should be unique and will be decorated with a domain to form
  /// a preference key.
  ///
  /// [onChanged] will be called upon value changes, the actual value change
  /// happens within the callback the function provides as its single
  /// parameter. I.e. simply pass [State.setState] as a function reference.
  /// This will do the job of updating the state appropriately.
  SingleStatefulAppOption(
    final String name,
    this.onChanged, [
    final bool defaultValue = false,
  ]) : key = "jim.io.tesserapp.options.$name" {
    SharedPreferences.getInstance().then((final prefs) {
      value = prefs.getBool(key) ?? defaultValue;
    });
  }

  void toggle() {
    value = !value;
  }
}

@immutable
class SingleStatelessAppOption {
  final SingleStatefulAppOption option;
  final bool _creationValue;

  SingleStatelessAppOption(this.option)
      : _creationValue = option.value;
}
