## Design overview of the Java Photo Editor

This Java-based Photo Editor is built with a clear focus on **Object-Oriented Programming (OOP)** principles. Below is a breakdown of how each OOP concept is applied in the architecture:

---

### 1. **Encapsulation**  
Encapsulation is achieved by keeping class data and methods grouped together, hiding internal implementations from the outside world.

- Classes like `BWFilter`, `BlueFilter`, `VignetteFilter`, `RotateTransform`, and `CropTransform` encapsulate the logic for applying their respective effects or transformations.
- Internal data (such as image matrices or configuration values) is kept private or protected, and access is provided only through well-defined public methods.
- The `ImageCanvas` class encapsulates the image and coordinates interaction between filters, transforms, and adjustments, acting as a controlled interface between the editor and the editing tools.

---

### 2. **Abstraction**  
Abstraction is handled using interfaces and abstract classes to define high-level behavior without revealing underlying details.

- Interfaces like `Filter`, `Transform`, and `Adjust` define contracts that concrete classes must fulfill.
- Classes such as `ImageAdjust`, `AbstractTransform`, and `AbstractFilter` provide partial implementation or structure while hiding complexity.
- This allows the main application (`PhotoEditorMain`) and `ImageCanvas` to work with generalized types without needing to know the specifics of how each filter or transform is implemented.

---

### 3. **Inheritance**  
Inheritance is used to promote code reuse and hierarchical organization.

- `AbstractFilter` is a superclass for `BWFilter`, `BlueFilter`, and `VignetteFilter`, providing shared methods and structure.
- Similarly, `AbstractTransform` acts as a base for `RotateTransform`, `FlipTransform`, and `CropTransform`, allowing them to inherit common transformation logic.
- This design minimizes code duplication and allows new filters or transforms to be added by simply extending the base class.

---

### 4. **Polymorphism**  
Polymorphism enables the system to use different object types interchangeably through a common interface.

- `ImageCanvas` can apply any `Filter` or `Transform` without knowing the specific class being used, as long as it implements the appropriate interface.
- This flexibility allows for easy expansion. For example, a new `SepiaFilter` or `ResizeTransform` can be integrated without modifying existing code—only implementing the interface and registering it in `ImageCanvas`.
- Runtime polymorphism ensures that the correct `apply()` method of the specific filter or transform class is invoked, even when accessed through an interface reference.

---
