import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import Tests.*;
@RunWith(Suite.class)
@SuiteClasses({CritterUnitTests.class, 
               EngineTest.class, 
               MapUnitTests.class,
               CellTest.class,
               LogTest.class,
               MapGeneratorTest.class,
               SurfaceTest.class,
               TowerTest.class})
public class TestSuite {   
} 
