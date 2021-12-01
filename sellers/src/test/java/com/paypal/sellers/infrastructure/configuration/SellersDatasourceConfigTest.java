package com.paypal.sellers.infrastructure.configuration;

import com.paypal.sellers.entity.FailedBankAccountInformation;
import com.paypal.sellers.entity.FailedSellersInformation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellersDatasourceConfigTest {

	@InjectMocks
	private SellersDatasourceConfig testObj;

	@Mock
	private EntityManagerFactoryBuilder entityManagerFactoryBuilderMock;

	@Mock
	private DataSource dataSourceMock;

	@Mock
	private EntityManagerFactoryBuilder.Builder builderMock;

	@Captor
	private ArgumentCaptor<Class> packagesArgumentCaptor;

	@Mock
	private LocalContainerEntityManagerFactoryBean sellersTransactionManagerMock;

	@Mock
	private EntityManagerFactory entityManagerFactoryMock;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private DataSourceProperties sellersDataSourcePropertiesMock;

	@Mock
	private DataSourceBuilder<DataSource> dataSourceBuilderMock;

	@Test
	void sellersDataSourceDataSourceProperties_shouldCreateANewDataSourcePropertiesInstance() {
		final DataSourceProperties result = testObj.sellersDataSourceDataSourceProperties();

		assertThat(result).isNotNull().isInstanceOf(DataSourceProperties.class);
	}

	@Test
	void sellersDatasource_shouldCreateANewDataSourceInstance() {
		doReturn(dataSourceBuilderMock).when(sellersDataSourcePropertiesMock).initializeDataSourceBuilder();
		doReturn(dataSourceMock).when(dataSourceBuilderMock).build();

		final DataSource result = testObj.sellersDatasource(sellersDataSourcePropertiesMock);

		assertThat(result).isSameAs(dataSourceMock);
	}

	@Test
	void sellersEntityManagerFactory_scanPackageJobExecutionInformationEntity() {
		when(entityManagerFactoryBuilderMock.dataSource(dataSourceMock)).thenReturn(builderMock);
		when(builderMock.packages(any(Class.class))).thenReturn(builderMock);

		testObj.sellersEntityManagerFactory(entityManagerFactoryBuilderMock, dataSourceMock);

		verify(builderMock).packages(packagesArgumentCaptor.capture());
		final List<Class> allValues = packagesArgumentCaptor.getAllValues();
		assertThat(allValues).hasSize(2).containsExactlyInAnyOrder(FailedBankAccountInformation.class,
				FailedSellersInformation.class);
	}

	@Test
	void sellersTransactionManager_shouldPlatformTransactionManager() {
		when(sellersTransactionManagerMock.getObject()).thenReturn(entityManagerFactoryMock);
		final PlatformTransactionManager result = testObj.sellersTransactionManager(sellersTransactionManagerMock);

		assertThat(result).isNotNull().isInstanceOf(PlatformTransactionManager.class);
	}

}
