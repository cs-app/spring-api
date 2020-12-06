package com.neta.teman.dawai.api.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neta.teman.dawai.api.applications.commons.ResourceUtils;
import com.neta.teman.dawai.api.applications.commons.UserCommons;
import com.neta.teman.dawai.api.models.dao.Employee;
import com.neta.teman.dawai.api.models.dao.Jabatan;
import com.neta.teman.dawai.api.models.dao.PangkatGolongan;
import com.neta.teman.dawai.api.models.dao.Role;
import com.neta.teman.dawai.api.models.repository.EmployeeRepository;
import com.neta.teman.dawai.api.models.repository.JabatanRepository;
import com.neta.teman.dawai.api.models.repository.PangkatGolonganRepository;
import com.neta.teman.dawai.api.models.repository.RoleRepository;
import com.neta.teman.dawai.api.plugins.simpeg.services.SimpegServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class RoleServiceImpl extends SimpegServiceImpl {

    @Autowired
    UserCommons userCommons;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    JabatanRepository jabatanRepository;

    @Autowired
    PangkatGolonganRepository pangkatGolonganRepository;

    public void initializeRole() {
        try {
            int[] types = new int[]{Types.INTEGER};
            String values = ResourceUtils.asString(resourceLoader.getResource("classpath:master/master_role.json"));
            List<Role> roleList = new ObjectMapper().readValue(values, new TypeReference<List<Role>>() {
            });
            for (Role role : roleList) {
                log.info("role data {} - {}", role.getName(), role.getDescription());
                Object[] params = new Object[]{role.getId()};
                String query = "SELECT COUNT(1) FROM APP_ROLE WHERE ID = ?";
                int foundInDB = jdbcTemplate.queryForObject(query, params, Integer.class);
                if (0 == foundInDB) {
                    String sql = "insert into APP_ROLE(id) values (?)";
                    int row = jdbcTemplate.update(sql, params, types);
                    log.info("create new role {}", row);
                }
                // update
                Role roleDB = roleRepository.findById(role.getId()).orElse(null);
                if (Objects.isNull(roleDB)) continue;
                BeanUtils.copyProperties(role, roleDB, "id");
                roleRepository.save(signature(roleDB));
            }
//            log.info("\n{}", values);
        } catch (JsonProcessingException e) {
            log.error("error read json role", e);
        }
    }


    public void initializePangkatGolongan() {
        try {
            int[] types = new int[]{Types.INTEGER};
            String values = ResourceUtils.asString(resourceLoader.getResource("classpath:master/master_pangkat_golongan.json"));
            List<PangkatGolongan> pangkatGolongans = new ObjectMapper().readValue(values, new TypeReference<List<PangkatGolongan>>() {
            });
            for (PangkatGolongan o : pangkatGolongans) {
                Object[] params = new Object[]{o.getId()};
                String query = "SELECT COUNT(1) FROM APP_PANGKAT_GOLONGAN WHERE ID = ?";
                int foundInDB = jdbcTemplate.queryForObject(query, params, Integer.class);
                if (0 == foundInDB) {
                    String sql = "insert into APP_PANGKAT_GOLONGAN(id) values (?)";
                    int row = jdbcTemplate.update(sql, params, types);
                    log.info("create new pangkat golongan {}", row);
                }
                // update
                PangkatGolongan golongan = pangkatGolonganRepository.findById(o.getId()).orElse(null);
                if (Objects.isNull(golongan)) continue;
                BeanUtils.copyProperties(o, golongan, "id", "documentPangkat");
                pangkatGolonganRepository.save(signature(golongan));
            }
        } catch (JsonProcessingException e) {
            log.error("error read json role", e);
        }
    }


    public void initializeJabatan() {
        try {
            int[] types = new int[]{Types.INTEGER};
            String values = ResourceUtils.asString(resourceLoader.getResource("classpath:master/master_jabatan.json"));
            List<Jabatan> jabatans = new ObjectMapper().readValue(values, new TypeReference<List<Jabatan>>() {
            });
            for (Jabatan o : jabatans) {
//                log.info("Pangkat Golongan data {} - {}", o.getGroup(), o.getGolongan());
                Object[] params = new Object[]{o.getId()};
                String query = "SELECT COUNT(1) FROM APP_JABATAN WHERE ID = ?";
                int foundInDB = jdbcTemplate.queryForObject(query, params, Integer.class);
                if (0 == foundInDB) {
                    String sql = "insert into APP_JABATAN(id) values (?)";
                    int row = jdbcTemplate.update(sql, params, types);
                    log.info("create new jabatan {}", row);
                }
                // update
                Jabatan jabatan = jabatanRepository.findById(o.getId()).orElse(null);
                if (Objects.isNull(jabatan)) continue;
                BeanUtils.copyProperties(o, jabatan, "id");
                jabatanRepository.save(signature(jabatan));
            }
//            log.info("\n{}", values);
        } catch (JsonProcessingException e) {
            log.error("error read json role", e);
        }
    }
}
