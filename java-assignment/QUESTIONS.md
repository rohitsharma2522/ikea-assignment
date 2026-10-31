# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
1. Database-access styles currently used

   - Active Record: StoreResource calls persistence methods directly through Store/PanacheEntity.
   - Repository: ProductResource uses a concrete Panache repository. Warehouse adds ports and use
     cases so its business logic does not depend on WarehouseRepository.

2. Trade-offs

   - Active Record is concise for CRUD but couples behavior to persistence.
   - Ports and repositories improve separation and testing but add classes and mapping.

3. Would I refactor?

   Incrementally, not uniformly:
   - Store first: after-commit synchronization is now isolated through immutable events; PATCH rules
     would be the next behavior to extract.
   - Product can remain a simple repository until it gains real business rules.
   - Warehouse should retain ports and use cases because replacement, history, and concurrency
     justify the abstraction.

   I would also move transaction ownership from REST methods toward use cases when atomicity is a
   business requirement.

4. Conclusion

   Refactor where complexity provides a payoff, not only to make every module look the same.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
1. The two endpoint styles

   - Contract-first: Warehouse is defined in YAML and Maven generates its interface and DTO.
   - Handwritten: Product and Store define endpoints directly with Java and JAX-RS annotations.

2. Trade-offs

   - Contract-first gives consumers a reviewable, testable contract but adds generator/build
     complexity.
   - Handwritten is faster for small APIs but can drift and mix transport with persistence concerns.

3. What this codebase demonstrates

   Store reuses its JPA entity as its wire model, so PATCH cannot distinguish omitted stock from
   zero. Dedicated DTOs become valuable once partial updates or validation differ from persistence.

4. Choice and conclusion

   I would use contract-first OpenAPI across team/system boundaries and handwritten endpoints for
   small internal CRUD. In either style, API DTOs should be separated from entities when their
   semantics differ.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
1. Prioritize by risk

   Prioritize failures that can corrupt data or expose uncommitted state: invalid capacity
   allocation, non-atomic replacement, concurrency conflicts, and premature legacy synchronization.

2. Testing order

   1. Plain unit/use-case tests: most cases belong here because the warehouse rules live in
      CreateWarehouseUseCase, ReplaceWarehouseUseCase, ArchiveWarehouseUseCase, and validators.
      Use in-memory ports and a fixed Clock; no Quarkus or database is needed.

   2. PostgreSQL integration tests: prove behavior mocks cannot—active-only queries, the partial
      unique index, advisory locks, rollback, and concurrent allocation. These must use real
      PostgreSQL because the implementation is database-specific.

   3. Focused REST/contract tests: verify mapping, status codes, and representative 400/404/409
      responses without repeating every domain-rule test.

   4. A few packaged-application tests: keep WarehouseEndpointIT small and cover only critical
      end-to-end workflows.

3. Coverage and maintenance

   Target behavioral and branch coverage of the listed rules, not a line percentage. Run unit tests
   on every code change, and run PostgreSQL integration and packaged-application tests before
   merging. Every regression gets a test at the lowest layer that can reproduce it, keeping the
   suite precise rather than merely larger.

4. Conclusion

   Put most tests around business rules, then use fewer database and API tests for transaction,
   concurrency, and contract behavior that unit tests cannot prove.
```
