# Final Assignment - Viva Preparation Guide
## Exact Code References & Pattern/Principle Breakdown

---

## 👤 MEMBER 1: JEEVITHA
### Pattern: **SINGLETON (Creational)**  
### Principle: **SINGLE RESPONSIBILITY PRINCIPLE (SRP)**

---

### 📍 FILE: [MainController.java](src/main/java/com/devrezaur/main/controller/MainController.java)

#### **EXACT CODE - Lines 1-18 (Singleton Implementation)**
```java
@Controller
@RequiredArgsConstructor
public class MainController {

  private final QuizService quizService;
```

---

### **WHY THIS IS A SINGLETON:**

| Aspect | Explanation | Code Reference |
|--------|-------------|-----------------|
| **Spring Container Management** | `@Controller` annotation tells Spring to create ONE instance of this class at startup and reuse it for all HTTP requests | Line 15 |
| **Instance Reuse** | The same MainController object handles multiple requests. When 100 users hit `/`, the same controller instance processes all requests | Spring lifecycle |
| **Lazy Initialization** | Spring creates the singleton when the application starts (or first accessed), not multiple times | Line 15 |
| **Thread-Safe Access** | Spring manages thread safety automatically for singleton beans | Spring guarantees |

**VIVA KEY POINT:** Explain that `@Controller` creates a singleton bean - there's only ONE MainController instance in the entire application, managed by Spring's Application Context.

---

### **EXACT CODE - Lines 16-49 (SRP Implementation)**

#### **Why SRP is Applied:**
```java
// Line 16-18: Constructor injection - only one dependency
private final QuizService quizService;

@GetMapping("/")
public String homePage() {
  return "home-page";
}

@PostMapping("/quiz")
public String quizPage(@RequestParam String username, Model model) {
  QuestionForm questionForm = quizService.getQuestions();
  model.addAttribute("questionForm", questionForm);
  model.addAttribute("username", username);
  return "quiz-page";
}

@PostMapping("/submit")
public String resultPage(@RequestParam String username, @ModelAttribute QuestionForm questionForm, Model model) {
  Result result = quizService.evaluateAndSaveResult(username, questionForm);
  model.addAttribute("result", result);
  return "result-page";
}

@GetMapping("/scoreboard")
public String scoreboardPage(Model model) {
  List<Result> results = quizService.getResults();
  model.addAttribute("results", results);
  return "scoreboard-page";
}
```

| SRP Aspect | Why It Matters | Code Example |
|------------|----------------|--------------|
| **ONE Responsibility** | MainController ONLY handles HTTP requests/responses. It does NOT handle business logic, scoring, or database operations | All methods call `quizService` for logic |
| **Delegation** | When scoring logic is needed, delegate to QuizService (line 36) | `scoringStrategy.calculateScore()` stays in QuizService, not here |
| **Clear Boundaries** | Controller ≠ Service ≠ Repository. Each class has one reason to change | MainController changes only when HTTP routes change |

**VIVA KEY POINTS TO MEMORIZE:**
- **Singleton:** "MainController is managed by Spring container as a singleton - only one instance exists for all requests"
- **SRP:** "Controller has ONE responsibility: map HTTP requests to business logic. It delegates scoring, data access to QuizService"
- **Evidence:** "The controller never directly accesses database or calculates scores - that's QuizService's job"

---

### **Complete Pattern Explanation for VIVA:**

**What is Singleton Pattern?**
- A creational pattern that ensures a class has only one instance
- Provides a global point of access to that instance

**In Your Code:**
- Spring's `@Controller` annotation creates a singleton bean
- When the app starts, Spring creates ONE MainController object
- This same object handles ALL incoming requests (thread-safe)

**What is Single Responsibility Principle?**
- A class should have ONE and ONLY ONE reason to change
- It should do ONE thing and do it well

**In Your Code:**
- MainController's reason to change: only if HTTP routes change
- It won't change if scoring logic changes (that's QuizService)
- It won't change if database structure changes (that's ResultRepo)

---

## 👤 MEMBER 2
### Pattern: **BUILDER (Creational)**  
### Principle: **ENCAPSULATION**

---

### 📍 FILE 1: [Result.java](src/main/java/com/devrezaur/main/model/Result.java)

#### **EXACT CODE - Lines 1-21**
```java
@Entity
@Table(name = "results")
@NoArgsConstructor
@AllArgsConstructor
@Builder           // ← BUILDER PATTERN HERE (Line 14)
@Data              // ← ENCAPSULATION (Line 15) - provides getters/setters
public class Result {

  @Id
  @GeneratedValue
  private int id;
  private String username;
  private int totalCorrect;
}
```

---

### **WHY THIS IS BUILDER PATTERN:**

| Feature | Explanation | Code Evidence |
|---------|-------------|-----------------|
| **@Builder Annotation** | Lombok generates a static `builder()` method that creates a Builder object | Line 14 |
| **Fluent Construction** | Allows step-by-step object construction with method chaining | See usage below |
| **Optional Fields** | Can set only required fields, skip optional ones | Builder handles null safely |
| **Immutability Support** | Can make fields final for immutability (thread-safe) | Fields can be final |

#### **EXACT CODE - Usage in ResultFactory.java (Lines 8-12)**
```java
public Result createResult(String username, int totalCorrect) {
    return Result.builder()           // Creates builder
        .username(username)            // Fluent method - sets username
        .totalCorrect(totalCorrect)    // Fluent method - sets totalCorrect
        .build();                      // Constructs Result object
}
```

**VIVA KEY POINT:** "Instead of `new Result(id, username, totalCorrect)`, we use builder pattern which is more readable and flexible. If we add 10 more fields later, code doesn't break."

---

### **ENCAPSULATION - Lines 9-15**

| Aspect | How It Works | Code |
|--------|------------|------|
| **Private Fields** | Fields are `private`, not public. External code cannot directly access/modify them | `private int id;` Line 10 |
| **Controlled Access** | Only through generated getters/setters | `@Data` generates getId(), setId() |
| **Validation Possible** | Custom setters can add validation (if needed) | Currently auto-generated by Lombok |
| **Data Protection** | Prevents accidental modification | Fields can't be accessed directly |

**Example of Encapsulation:**
```java
// ❌ WITHOUT ENCAPSULATION (BAD):
Result result = new Result();
result.totalCorrect = -100;  // INVALID - allows negative score!

// ✅ WITH ENCAPSULATION (GOOD):
Result result = Result.builder()
    .totalCorrect(100)
    .build();
// Now we can add validation in setter if needed
```

**VIVA KEY POINTS TO MEMORIZE:**
- **Builder:** "Lombok's @Builder generates builder() method. Instead of new Result(1, 'user', 100), we use Result.builder().username('user').totalCorrect(100).build() - more readable and flexible"
- **Encapsulation:** "Fields are private. Access only through getters/setters. Protects data integrity and allows future validation logic"
- **Why It Matters:** "If tomorrow we need to validate username length, we can add validation in the setter without breaking client code"

---

### 📍 FILE 2: [Question.java](src/main/java/com/devrezaur/main/model/Question.java)

#### **EXACT CODE - Lines 1-21**
```java
@NoArgsConstructor
@AllArgsConstructor
@Data              // ← ENCAPSULATION: Generates getters/setters for all fields
@Entity
@Table(name = "questions")
public class Question {

  @Id
  @GeneratedValue
  private int quesId;
  private String title;
  private String optionA;
  private String optionB;
  private String optionC;
  private int correctAns;
  private int selectedAns;
}
```

**Encapsulation in Question.java:**
- All 7 fields are `private` (lines 11-16)
- `@Data` generates getters & setters for each field
- External code accesses fields only through generated methods: `question.getTitle()`, `question.setTitle("...")`

**VIVA KEY POINT:** "@Data annotation provides all getters and setters. This is encapsulation - fields are private, controlled access through methods."

---

### **Complete Pattern Explanation for VIVA:**

**What is Builder Pattern?**
- A creational pattern for constructing complex objects step-by-step
- Separates construction from representation
- Especially useful when objects have many optional parameters

**In Your Code:**
```java
// Traditional way (hard to read with many params):
new Result(1, "user1", 0, "timestamp", 50, "status", "grade");

// Builder way (readable, flexible):
Result.builder()
  .username("user1")
  .totalCorrect(0)
  .build();
```

**What is Encapsulation?**
- Bundling data (fields) and methods together
- Hiding internal details from the outside world
- Providing controlled access through getters/setters

**In Your Code:**
- `private int id` - hidden from outside
- `getId()` / `setId()` - controlled access
- If validation needed later, add it in setter without breaking code using the field

---

## 👤 MEMBER 3
### Pattern: **FACTORY (Creational)**  
### Principle: **OPEN/CLOSED PRINCIPLE (OCP)**

---

### 📍 FILE 1: [ResultFactory.java](src/main/java/com/devrezaur/main/service/factory/ResultFactory.java)

#### **EXACT CODE - Lines 1-12**
```java
@Component
public class ResultFactory {

  public Result createResult(String username, int totalCorrect) {
    return Result.builder()
        .username(username)
        .totalCorrect(totalCorrect)
        .build();
  }
}
```

---

### **WHY THIS IS FACTORY PATTERN:**

| Factory Aspect | Explanation | Code |
|----------------|-------------|------|
| **Centralized Creation** | ALL Result object creation happens in ONE place: ResultFactory | Lines 5-9 |
| **Encapsulates Logic** | Hides how Result objects are built (could add validation, timestamps, etc. later) | Uses Builder internally |
| **Single Responsibility** | Factory's ONLY job: create Result objects correctly | One method `createResult()` |
| **Easy Modification** | To change creation logic, modify ONLY ONE file | Don't need to find all `new Result(...)` calls |

#### **EXACT USAGE - QuizService.java (Line 41)**
```java
@Service
@RequiredArgsConstructor
public class QuizService {
  
  private final ResultFactory resultFactory;  // Line 21 - Injected

  public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
    int totalCorrect = scoringStrategy.calculateScore(questionForm);
    Result result = resultFactory.createResult(username, totalCorrect);  // Line 41 - Used here
    resultRepo.save(result);
    return result;
  }
}
```

**Why Factory Here?**
- QuizService doesn't directly do `new Result()` or `Result.builder()...build()`
- Delegates to ResultFactory
- If tomorrow we need to add timestamps, difficulty levels, user preferences to Result, we modify ONLY ResultFactory

---

### **OPEN/CLOSED PRINCIPLE - OCP**

#### **What OCP Means:**
- Classes should be **OPEN for extension** (can add new features)
- But **CLOSED for modification** (don't change existing code)

#### **EXACT CODE - How OCP Applied:**

**BEFORE Factory Pattern (Violates OCP):**
```java
// QuizService.java - TIGHTLY COUPLED
public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
    int totalCorrect = scoringStrategy.calculateScore(questionForm);
    
    // Direct creation - if Result changes, this code breaks
    Result result = Result.builder()
        .username(username)
        .totalCorrect(totalCorrect)
        .build();
    
    resultRepo.save(result);
    return result;
}

// If later we need to add timestamp:
// ❌ Must modify QuizService code - VIOLATES OCP
Result result = Result.builder()
    .username(username)
    .totalCorrect(totalCorrect)
    .timestamp(LocalDateTime.now())  // ← Need to add here
    .build();
```

**AFTER Factory Pattern (Follows OCP):**
```java
// QuizService.java - DECOUPLED
public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
    int totalCorrect = scoringStrategy.calculateScore(questionForm);
    
    // Delegates to factory
    Result result = resultFactory.createResult(username, totalCorrect);  // ✅ No change needed
    
    resultRepo.save(result);
    return result;
}

// If we need to add timestamp:
// ✅ Modify ONLY ResultFactory - QuizService unchanged
public Result createResult(String username, int totalCorrect) {
    return Result.builder()
        .username(username)
        .totalCorrect(totalCorrect)
        .timestamp(LocalDateTime.now())  // ← Add here only
        .build();
}
```

| Scenario | Without Factory | With Factory |
|----------|-----------------|--------------|
| **Result class changes** | Must update QuizService + other places | Update only ResultFactory |
| **Adding new fields** | Find all Result creations (10+ places) | Modify 1 factory method |
| **Testing** | Mock Result creation in multiple places | Mock 1 factory |
| **Future requirements** | High change impact | Low change impact |

---

### **VIVA KEY POINTS TO MEMORIZE:**

- **Factory Pattern:** "All Result object creation is centralized in ResultFactory. Instead of scattered `new Result()` calls, we have ONE place to create results."
- **OCP:** "QuizService is CLOSED for modification - when Result structure changes, we only modify ResultFactory. QuizService code stays untouched."
- **Real Benefit:** "Tomorrow if we need to add timestamp, difficulty assessment, or user metadata to Result, we change only ResultFactory. No other class needs modification."
- **Evidence:** "See line 41 in QuizService - it uses `resultFactory.createResult()`. If we add 5 new fields to Result, this line never changes."

---

### **Complete Pattern Explanation for VIVA:**

**What is Factory Pattern?**
- A creational pattern that provides a method to create objects without specifying exact classes
- Centralizes object creation logic
- Makes code flexible and maintainable

**In Your Code:**
- `ResultFactory.createResult()` is the factory method
- Instead of `new Result()` in multiple places, use the factory
- All Result creation logic lives in ONE place

**What is Open/Closed Principle?**
- Open for extension: can add new features without modifying existing code
- Closed for modification: existing working code should NOT change

**In Your Code:**
- Factory makes QuizService CLOSED for modification
- Can extend Result with new fields by modifying only ResultFactory (extension through factory modification)
- QuizService remains unchanged when Result changes

---

## 👤 MEMBER 4
### Pattern: **STRATEGY (Behavioral)**  
### Principle: **DEPENDENCY INVERSION PRINCIPLE (DIP)**

---

### 📍 FILE 1: [ScoringStrategy.java](src/main/java/com/devrezaur/main/service/scoring/ScoringStrategy.java)

#### **EXACT CODE - Lines 1-7**
```java
public interface ScoringStrategy {

  int calculateScore(QuestionForm questionForm);
}
```

**This is the ABSTRACTION layer** - Strategy interface that defines the contract for all scoring strategies.

---

### 📍 FILE 2: [ExactMatchScoringStrategy.java](src/main/java/com/devrezaur/main/service/scoring/ExactMatchScoringStrategy.java)

#### **EXACT CODE - Lines 1-21**
```java
@Component
public class ExactMatchScoringStrategy implements ScoringStrategy {

  @Override
  public int calculateScore(QuestionForm questionForm) {
    int totalCorrect = 0;

    for (Question question : questionForm.getQuestions()) {
      if (question.getCorrectAns() == question.getSelectedAns()) {  // Line 11 - scoring logic
        totalCorrect++;
      }
    }

    return totalCorrect;
  }
}
```

**This is ONE CONCRETE STRATEGY** - implements the scoring interface with specific logic (exact match).

---

### **WHY THIS IS STRATEGY PATTERN:**

| Strategy Aspect | Explanation | Code Evidence |
|-----------------|-------------|-----------------|
| **Strategy Interface** | Defines common behavior all strategies must implement | `ScoringStrategy` interface |
| **Concrete Strategy** | `ExactMatchScoringStrategy` - one way to calculate score | Lines 1-21 |
| **Interchangeable** | Can swap strategies at runtime without code changes | See usage below |
| **Different Algorithms** | Easy to add `PartialCreditScoringStrategy` later | Just implement interface |

#### **EXACT USAGE - QuizService.java (Lines 1-43)**

```java
@Service
@RequiredArgsConstructor
public class QuizService {

  private final int NUM_OF_QUES = 5;

  private final QuestionRepo questionRepo;
  private final ResultRepo resultRepo;
  private final ScoringStrategy scoringStrategy;    // Line 21 - Depends on INTERFACE, not concrete class
  private final ResultFactory resultFactory;

  public QuestionForm getQuestions() {
    // ... question selection logic
  }

  public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
    int totalCorrect = scoringStrategy.calculateScore(questionForm);  // Line 39 - Calls strategy
    Result result = resultFactory.createResult(username, totalCorrect);
    resultRepo.save(result);
    return result;
  }
}
```

**KEY LINE:** Line 21 shows `ScoringStrategy` (interface), NOT `ExactMatchScoringStrategy` (concrete class).

---

### **STRATEGY PATTERN - How It Works:**

**WITHOUT Strategy Pattern (BAD):**
```java
public class QuizService {
    
    public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
        int totalCorrect = 0;
        
        // Scoring logic hardcoded here - TIGHTLY COUPLED
        for (Question question : questionForm.getQuestions()) {
            if (question.getCorrectAns() == question.getSelectedAns()) {
                totalCorrect++;
            }
        }
        
        // Tomorrow: need partial credit? Modify this method
        // Future: need weighted scoring? Modify this method
        // Problem: QuizService knows HOW to score - violates SRP
    }
}
```

**WITH Strategy Pattern (GOOD):**
```java
public class QuizService {
    private final ScoringStrategy scoringStrategy;  // ← INJECTED STRATEGY
    
    public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
        // Delegates to strategy - QuizService doesn't know HOW
        int totalCorrect = scoringStrategy.calculateScore(questionForm);
        
        // Tomorrow: need partial credit? Create PartialCreditScoringStrategy
        // Future: need weighted scoring? Create WeightedScoringStrategy
        // QuizService stays UNCHANGED
    }
}
```

---

### **DEPENDENCY INVERSION PRINCIPLE - DIP**

#### **What DIP Means:**
- **High-level modules** should NOT depend on **low-level modules**
- Both should depend on **abstractions** (interfaces)
- Depend on abstractions, NOT concrete implementations

#### **EXACT CODE - DIP Violation vs. DIP Applied:**

**BEFORE DIP (Violates DIP):**
```java
@Service
public class QuizService {
    
    // ❌ Depends on CONCRETE class ExactMatchScoringStrategy
    private final ExactMatchScoringStrategy scoring;
    
    public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
        // Tightly coupled - if we want PartialCreditScoringStrategy, 
        // must change QuizService code
        int totalCorrect = scoring.calculateScore(questionForm);
    }
}
```

**AFTER DIP (Applied DIP):**
```java
@Service
@RequiredArgsConstructor
public class QuizService {
    
    // ✅ Depends on ABSTRACTION ScoringStrategy (interface)
    private final ScoringStrategy scoringStrategy;
    // Line 21 - This is the DIP magic!
    
    public Result evaluateAndSaveResult(String username, QuestionForm questionForm) {
        // Can work with ANY implementation of ScoringStrategy
        int totalCorrect = scoringStrategy.calculateScore(questionForm);
        // Today: ExactMatchScoringStrategy
        // Tomorrow: PartialCreditScoringStrategy
        // Future: WeightedScoringStrategy
        // QuizService code NEVER changes
    }
}
```

---

### **DIP - Dependency Flow Diagram:**

```
WITHOUT DIP (❌):
QuizService → ExactMatchScoringStrategy (concrete class)
                ↑
            Depends on IMPLEMENTATION

WITH DIP (✅):
QuizService → ScoringStrategy (interface) ← ExactMatchScoringStrategy
                ↑
            Depends on ABSTRACTION
```

---

### **DIP - Real World Impact:**

| Scenario | Without DIP | With DIP |
|----------|------------|---------|
| **Add partial credit scoring** | Modify QuizService to accept PartialCreditScoringStrategy parameter | Create PartialCreditScoringStrategy.java, NO other changes |
| **Change implementation** | Change type from ExactMatchScoringStrategy to other class everywhere | Change implementation in Spring config/factory, code unchanged |
| **Unit Testing** | Must mock ExactMatchScoringStrategy specifically | Create MockScoringStrategy, inject it - QuizService doesn't care |
| **New scoring in future** | Modify multiple classes | Add new class implementing ScoringStrategy interface |

---

### **VIVA KEY POINTS TO MEMORIZE:**

- **Strategy Pattern:** "ScoringStrategy is an interface. ExactMatchScoringStrategy implements it. Tomorrow we can create PartialCreditScoringStrategy implementing same interface. All strategies are interchangeable."
- **DIP:** "QuizService depends on ScoringStrategy INTERFACE, not ExactMatchScoringStrategy class. This is inversion - high-level module (QuizService) depends on abstraction, not low-level concrete class."
- **Real Benefit:** "Tomorrow if we need partial credit scoring, we just create a new class implementing ScoringStrategy. QuizService line 39 never changes - it's decoupled from concrete implementations."
- **Evidence:** "See line 21 in QuizService: `private final ScoringStrategy` NOT `ExactMatchScoringStrategy`. That's DIP - depends on interface, not concrete class."

---

### **Complete Pattern Explanation for VIVA:**

**What is Strategy Pattern?**
- A behavioral pattern that defines a family of algorithms
- Encapsulates each algorithm in a separate class
- Makes algorithms interchangeable at runtime

**In Your Code:**
- `ScoringStrategy` interface = Strategy abstraction
- `ExactMatchScoringStrategy` = Concrete algorithm
- Tomorrow: `PartialCreditScoringStrategy` = Another algorithm
- All can be swapped without changing QuizService

**What is Dependency Inversion Principle?**
- High-level code should NOT depend on low-level code
- Both should depend on abstractions
- Code against interfaces, not concrete classes

**In Your Code:**
- `QuizService` (high-level) depends on `ScoringStrategy` (abstraction)
- NOT on `ExactMatchScoringStrategy` (concrete)
- Spring's dependency injection automatically provides the right implementation
- If you want different scoring, provide different implementation - no code change

---

## 🎯 VIVA QUICK REFERENCE - MEMORIZE THIS

### Member 1 (Jeevitha):
```
Singleton: Spring creates ONE MainController instance via @Controller
           All requests use same instance
SRP:       MainController = HTTP mapping only
           QuizService = business logic
           ResultRepo = data access
Reason to change: MainController only changes when routes change
```

### Member 2:
```
Builder: Result.builder().username(...).totalCorrect(...).build()
         More readable than new Result(1, "user", 100)
         
Encapsulation: private fields + getters/setters
               Hides internals, allows future validation
               Fields protected from invalid values
```

### Member 3:
```
Factory: ResultFactory.createResult() - ONE place for Result creation
         
OCP:     When Result changes, modify ONLY ResultFactory
         QuizService stays closed for modification
         This is EXTENSION through factory modification
```

### Member 4:
```
Strategy: ScoringStrategy interface
          ExactMatchScoringStrategy = concrete algorithm
          Tomorrow: add PartialCreditScoringStrategy
          All interchangeable
          
DIP:      QuizService depends on ScoringStrategy (interface)
          NOT ExactMatchScoringStrategy (concrete)
          This is INVERSION - depends on abstraction
          Decouples high-level from low-level modules
```

---

## 📋 What Each Person Should Study for Viva:

### Jeevitha:
- [ ] Why `@Controller` creates a singleton bean
- [ ] Explain: "One MainController handles all requests"
- [ ] SRP: "Controller's one job - map HTTP requests"
- [ ] Point to line 36: QuizService handles business logic, controller doesn't
- [ ] Difference: "If scoring logic changes, MainController code doesn't need to change"

### Member 2:
- [ ] Show `@Builder` annotation → generates builder() method
- [ ] Show ResultFactory lines 5-9: builder() pattern usage
- [ ] Explain: "More readable than constructor with 10 parameters"
- [ ] Encapsulation: "Fields are private, accessed through getters/setters"
- [ ] Future-proof: "If we add fields, getters/setters generated automatically"

### Member 3:
- [ ] Factory pattern: "Centralized creation in ONE place"
- [ ] Line 41 in QuizService: calls factory, doesn't create directly
- [ ] OCP: "Adding fields to Result? Modify only ResultFactory"
- [ ] QuizService unchanged: "This is open/closed principle"
- [ ] Show before/after: direct creation vs factory creation

### Member 4:
- [ ] Strategy: ScoringStrategy interface (line 7)
- [ ] ExactMatchScoringStrategy implements it (line 3)
- [ ] Tomorrow: create new strategy, same interface
- [ ] DIP: Line 21 - depends on `ScoringStrategy`, not `ExactMatchScoringStrategy`
- [ ] Benefit: "Can swap implementations without changing QuizService"

---

## 🔍 EXACT LINES FOR VIVA ANSWERS

```
MainController.java:
- Singleton: Line 15 - @Controller
- SRP: Line 21 - only one responsibility: HTTP routing

Result.java:
- Builder: Line 14 - @Builder annotation
- Encapsulation: Lines 10-13 - private fields

ResultFactory.java:
- Factory: Lines 5-9 - createResult() method

QuizService.java:
- Factory Usage: Line 41 - resultFactory.createResult()
- Strategy: Line 21 - private final ScoringStrategy
- DIP: Line 21 - depends on interface, not concrete class

ScoringStrategy.java:
- Strategy Interface: Lines 1-7

ExactMatchScoringStrategy.java:
- Concrete Strategy: Lines 1-21
- Algorithm: Lines 11-12 - scoring logic
```

---

**Good Luck with Your Presentation! 🚀**
