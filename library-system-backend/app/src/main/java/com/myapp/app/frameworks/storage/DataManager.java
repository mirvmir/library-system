package com.myapp.app.frameworks.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.myapp.app.frameworks.Config;

public class DataManager {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private String path;

    //@Inject
    private Config config;

//    @Inject
//    private BookModelRepository modelRepo;
//    @Inject
//    private BookUnitRepository unitRepo;
//    @Inject
//    private CustomerRepository customerRepo;
//    @Inject
//    private OrderRepository orderRepo;
//    @Inject
//    private BookRequestEventRepository requestRepo;

//    public void init() {
//        this.path = config.getDataStorePath();
//    }
//
//    public void save() throws Exception {
//        DataStore store = new DataStore(
//                modelRepo.findAll(),
//                unitRepo.findAll(),
//                customerRepo.findAll(),
//                orderRepo.findAll(),
//                requestRepo.getAll()
//        );
//
//        mapper.writeValue(new File(path), store);
//    }
//
//    public void load() throws Exception {
//        File file = new File(path);
//        if (!file.exists()) {
//            throw new IllegalArgumentException("Файл данных "
//                    + path
//                    + " не найден. Будет создан новый при сохранении.");
//        }
//
//        DataStore store = mapper.readValue(file, DataStore.class);
//
//        modelRepo.loadAll(store.getModels());
//        unitRepo.loadAll(store.getUnits());
//        customerRepo.loadAll(store.getCustomers());
//        orderRepo.loadAll(store.getOrders());
//        requestRepo.loadAll(store.getRequests());
//    }
}
